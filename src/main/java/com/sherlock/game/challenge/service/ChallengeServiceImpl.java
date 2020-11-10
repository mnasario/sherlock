package com.sherlock.game.challenge.service;

import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.ChallengeSummary;
import com.sherlock.game.challenge.exception.ChallengeRoomNotFoundException;
import com.sherlock.game.challenge.exception.ChallengeSummaryNotFoundException;
import com.sherlock.game.challenge.repository.ChallengeRoomRepository;
import com.sherlock.game.challenge.repository.ChallengeSummaryRepository;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.exception.PlayerNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.websocket.Session;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.sherlock.game.support.GameHandler.generateRandomCode;

@Slf4j
@Service
@AllArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private static Map<String, ChallengeRoom> challengeRoomMap = new ConcurrentHashMap<>();

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
    public Envelop login(String gameId, Player player) {
        //String key = getKey(gameId, player);
        //gamesMap.putIfAbsent(key, session);
        //broadcast(gameId, player, player + " is online!");
        return null;
    }

    @Override
    public Envelop processMessage(String gameId, Envelop message, String playerName) {

        //log.info("Type: " + message.getType() + " - Value: " + message.getPayload());
        //broadcast(gameId, player, ">> " + player + ": " + message.getPayload());

        return null;
    }

    @Override
    public Envelop summarize(String gameId, String playerName) {

        //String key = getKey(gameId, player);
        //gamesMap.remove(key);
        //broadcast(gameId, player, "Player " + player + " left");

        return null;
    }

    @Override
    public Envelop processError(String gameId, String playerName, Throwable throwable) {

        //String key = getKey(gameId, player);
        //gamesMap.remove(key);
        //log.error("onError", throwable);
        //broadcast(gameId, player, "Player " + player + " left on error: " + throwable);

        return null;
    }

    private void broadcast(String gameId, Envelop message) {

        getRoom(gameId).getPlayers().forEach(player -> sendMessageTo(player, message));
    }

    private void sendMessageTo(Player player, Envelop envelop) {

        Session session = player.getSession();
        log.info("Session: " + session.getId() + " - Player from: " + player.getName() + " - Message: " + envelop.getPayload());
        session.getAsyncRemote().sendObject(envelop, result -> {
            if (result.getException() != null)
                log.error("Unable to send message content from player " + player, result.getException());
        });
    }
}
