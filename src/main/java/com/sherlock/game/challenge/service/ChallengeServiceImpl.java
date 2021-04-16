package com.sherlock.game.challenge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.ChallengeSummary;
import com.sherlock.game.challenge.domain.MessageRequest;
import com.sherlock.game.challenge.exception.ChallengeRoomNotFoundException;
import com.sherlock.game.challenge.exception.ChallengeSummaryNotFoundException;
import com.sherlock.game.challenge.processor.ChallengeMessageProcessor;
import com.sherlock.game.challenge.repository.ChallengeRoomRepository;
import com.sherlock.game.challenge.repository.ChallengeSummaryRepository;
import com.sherlock.game.core.domain.Credentials;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import com.sherlock.game.core.exception.MessageProcessorNotFoundException;
import com.sherlock.game.core.exception.PlayerNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import static com.sherlock.game.core.domain.message.Subject.GAME_SUMMARIZED;
import static com.sherlock.game.core.domain.message.Subject.PLAYER_JOINED;
import static com.sherlock.game.core.domain.message.Type.INFO;
import static com.sherlock.game.support.GameHandler.generateRandomCode;

@Slf4j
@Service
@AllArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private static final Map<String, ChallengeRoom> challengeRoomMap = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;
    private final Map<Subject, ChallengeMessageProcessor> messageProcessorMap;
    private final ChallengeRoomRepository challengeRoomRepository;
    private final ChallengeSummaryRepository challengeSummaryRepository;

    @Override
    public ChallengeRoom insert(ChallengeConfig config) {

        Assert.notNull(config, "Challenge config is required");
        Assert.notNull(config.getPlayerHost(), "Player host is required");
        Assert.notNull(config.getTimerInSeconds(), "Time of game is required");
        Assert.notNull(config.getMarkersAmount(), "Amount of markers is required");
        String gameId = generateRandomCode(20);
        ChallengeRoom room = ChallengeRoom.builder()
                .mapper(mapper)
                .gameId(gameId)
                .gameConfig(config)
                .playersMap(new ConcurrentHashMap<>())
                .build();

        challengeRoomMap.putIfAbsent(gameId, challengeRoomRepository.save(room));
        return room;
    }

    @Override
    public ChallengeRoom getRoom(String gameId) {

        Assert.notNull(gameId, "Game id is required");
        return Optional.ofNullable(challengeRoomMap.get(gameId))
                .orElseThrow(ChallengeRoomNotFoundException::new);
    }

    @Override
    public ChallengeSummary getSummary(String gameId) {

        Assert.notNull(gameId, "Game id is required");
        return challengeSummaryRepository.findById(gameId)
                .orElseThrow(ChallengeSummaryNotFoundException::new);
    }

    @Override
    public Player getPlayer(String gameId, String playerName) {

        Assert.notNull(gameId, "Game id is required");
        Assert.notNull(playerName, "Player name is required");

        ChallengeRoom room = getRoom(gameId);
        Player playerHost = room.getGameConfig().getPlayerHost();
        if (playerName.equalsIgnoreCase((playerHost.getName()))) return playerHost;

        return Optional.ofNullable(room.getPlayers())
                .map(players ->
                        players.stream()
                                .filter(p -> playerName.equalsIgnoreCase(p.getName()))
                                .findFirst()
                                .orElseThrow(PlayerNotFoundException::new))
                .orElseThrow(PlayerNotFoundException::new);
    }

    @Override
    public Envelop login(Credentials credentials) {

        validateCredentials(credentials);
        ChallengeRoom room = getRoom(credentials.getGameId());
        if (room.isAvailable()) room.addPlayerToRoom(credentials);
        return room.broadcast(INFO, PLAYER_JOINED, room);
    }

    @Override
    public Envelop processMessage(Credentials credentials, Envelop message) {

        validateCredentials(credentials);
        Assert.notNull(message, "Envelop is required");
        Assert.notNull(message.getType(), "Envelop type is required");
        Assert.notNull(message.getSubject(), "Envelop subject is required");

        ChallengeRoom room = getRoom(credentials.getGameId());
        Player player = getPlayer(credentials.getGameId(), credentials.getPlayerName());
        ChallengeMessageProcessor messageProcessor = getMessageProcessor(message.getSubject());
        return messageProcessor.process(MessageRequest.builder().room(room).player(player).envelop(message).build());
    }

    @Override
    public Envelop summarize(Credentials credentials) {

        validateCredentials(credentials);
        ChallengeRoom room = getRoom(credentials.getGameId());
        Player player = getPlayer(credentials.getGameId(), credentials.getPlayerName());
        ChallengeMessageProcessor messageProcessor = getMessageProcessor(GAME_SUMMARIZED);
        return messageProcessor.process(MessageRequest.builder().room(room).player(player).build());
    }

    @PostConstruct
    public void scheduleGameCleaner() {
        final int period = 60 * 60 * 1000;
        log.debug("Schedule the game cleaner for each intervals of {} millis", period);
        TimerTask task = new TimerTask() {
            public void run() {
                triggerGameCleaner();
            }
        };
        new Timer().scheduleAtFixedRate(task, 0, period);
    }

    private void validateCredentials(Credentials credentials) {

        Assert.notNull(credentials, "Credentials is required");
        Assert.notNull(credentials.getGameId(), "Game id is required");
        Assert.notNull(credentials.getPlayerName(), "Player name is required");
        Assert.notNull(credentials.getSession(), "Player session is required");
        Assert.isTrue(credentials.getPlayerName().matches("^[\\w\\-_\\s]+$"), "Player name should be alphanumeric");
    }

    private ChallengeMessageProcessor getMessageProcessor(Subject subject) {
        return Optional.ofNullable(messageProcessorMap.get(subject))
                .orElseThrow(MessageProcessorNotFoundException::new);
    }

    private void triggerGameCleaner() {
        log.info("Starting to clean the game challenge cache");
        challengeRoomMap.forEach((gameId, room) -> {
            if (room.hasFinished()) {
                log.debug("Game Id {} has finished and it will be removed from cache", gameId);
                challengeRoomRepository.save(room);
                challengeRoomMap.remove(gameId);
            }
        });
        log.info("Process to clean the game challenge cache has finished");
    }
}
