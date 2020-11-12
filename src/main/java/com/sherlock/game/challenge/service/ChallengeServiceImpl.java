package com.sherlock.game.challenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.ChallengeSummary;
import com.sherlock.game.challenge.exception.ChallengeRoomNotFoundException;
import com.sherlock.game.challenge.exception.ChallengeSummaryNotFoundException;
import com.sherlock.game.challenge.repository.ChallengeRoomRepository;
import com.sherlock.game.challenge.repository.ChallengeSummaryRepository;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import com.sherlock.game.core.domain.message.Type;
import com.sherlock.game.core.exception.PlayerNotFoundException;
import com.sherlock.game.support.MessageProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.websocket.Session;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.sherlock.game.core.domain.message.Subject.GAME_FAILED;
import static com.sherlock.game.core.domain.message.Subject.PLAYER_JOINED;
import static com.sherlock.game.core.domain.message.Type.ERROR;
import static com.sherlock.game.core.domain.message.Type.INFO;
import static com.sherlock.game.support.GameHandler.generateRandomCode;
import static java.util.Objects.isNull;

@Slf4j
@Service
@AllArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private static Map<String, ChallengeRoom> challengeRoomMap = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;
    private final Map<Subject, MessageProcessor> messageProcessorMap;
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

    private ChallengeRoom getRoom(Session session, String gameId) {
        try {
            return getRoom(gameId);
        } catch (ChallengeSummaryNotFoundException e) {
            processError(session, e);
            throw e;
        }
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

    private Player getPlayer(Session session, String gameId, String playerName) {
        try {
            return getPlayer(gameId, playerName);
        } catch (PlayerNotFoundException e) {
            processError(session, e);
            throw e;
        }
    }

    @Override
    public Envelop login(Session session, String gameId, String playerName) {

        Assert.notNull(gameId, "Game id is required");
        Assert.notNull(playerName, "Player name is required");
        Assert.notNull(session, "Player session is required");

        ChallengeRoom room = getRoom(session, gameId);
        room.getPlayersMap().putIfAbsent(playerName, Player.builder().name(playerName).session(session).build());
        return room.broadcast(createMessage(INFO, PLAYER_JOINED, room));
    }

    @Override
    public Envelop processMessage(Session session, String gameId, String playerName, Envelop message) {

        Assert.notNull(gameId, "Game id is required");
        Assert.notNull(playerName, "Player name is required");
        Assert.notNull(message, "Envelop is required");
        Assert.notNull(message.getType(), "Envelop type is required");
        Assert.notNull(message.getSubject(), "Envelop subject is required");
        Assert.notNull(message.getPayload(), "Envelop content is required");

        MessageProcessor messageProcessor = messageProcessorMap.get(message.getSubject());
        if (isNull(messageProcessor)) {
            String errorMessage = "Processor not implemented to subject " + message.getSubject();
            return processError(session, new RuntimeException(errorMessage));
        }

        ChallengeRoom room = getRoom(session, gameId);
        Player player = getPlayer(session, gameId, playerName);
        return messageProcessor.process(room, player, message);
    }

    @Override
    public Envelop summarize(Session session, String gameId, String playerName) {

        Assert.notNull(gameId, "Game id is required");
        Assert.notNull(playerName, "Player name is required");

        //ChallengeRoom room = getRoom(session, gameId);
        //Player player = getPlayer(session, gameId, playerName);
        //ChallengeSummary summary = ChallengeSummary.builder().build();
        return null;
    }

    @Override
    public Envelop processError(Session session, Throwable throwable) {

        String errorMessage = String.format("{'errorMessage': '%s'}", throwable.getMessage());
        log.error(errorMessage, throwable);

        Envelop message = Envelop.builder()
                .type(ERROR)
                .subject(GAME_FAILED)
                .payload(errorMessage)
                .build();
        session.getAsyncRemote().sendObject(message);
        return message;
    }

    private <T> Envelop createMessage(Type type, Subject subject, T object) {

        try {

            return Envelop.builder()
                    .type(type)
                    .subject(subject)
                    .payload(mapper.writeValueAsString(object))
                    .build();

        } catch (JsonProcessingException e) {

            String error = "Error to convert the object to payload. " + e.getMessage();
            log.error(error, e);

            return Envelop.builder()
                    .type(ERROR)
                    .subject(subject)
                    .payload(String.format("{errorMessage: %s}", error))
                    .build();
        }
    }

}
