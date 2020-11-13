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

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.sherlock.game.core.domain.message.Subject.*;
import static com.sherlock.game.core.domain.message.Type.INFO;
import static com.sherlock.game.support.GameHandler.generateRandomCode;

@Slf4j
@Service
@AllArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private static Map<String, ChallengeRoom> challengeRoomMap = new ConcurrentHashMap<>();
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
        Assert.notNull(message.getPayload(), "Envelop content is required");

        ChallengeRoom room = getRoom(credentials.getGameId());
        Player player = getPlayer(credentials.getGameId(), credentials.getPlayerName());
        ChallengeMessageProcessor messageProcessor = getMessageProcessor(message);
        return messageProcessor.process(MessageRequest.builder().room(room).player(player).envelop(message).build());
    }

    @Override
    public Envelop summarize(Credentials credentials) {

        validateCredentials(credentials);
        ChallengeRoom room = getRoom(credentials.getGameId());
        Player player = getPlayer(credentials.getGameId(), credentials.getPlayerName());
        //TODO Process the result to player here

        if (player.hasNotFinishedGame()) room.broadcast(INFO, PLAYER_LEFT, player);
        if (room.isNotEnded()) return null;
        ChallengeSummary summary = ChallengeSummary.builder().build();
        return room.broadcast(INFO, GAME_SUMMARIZED, summary);
    }

    private void validateCredentials(Credentials credentials) {
        Assert.notNull(credentials, "Credentials is required");
        Assert.notNull(credentials.getGameId(), "Game id is required");
        Assert.notNull(credentials.getPlayerName(), "Player name is required");
        Assert.notNull(credentials.getSession(), "Player session is required");
    }

    private ChallengeMessageProcessor getMessageProcessor(Envelop message) {
        return Optional.ofNullable(messageProcessorMap.get(message.getSubject()))
                .orElseThrow(MessageProcessorNotFoundException::new);
    }
}
