package com.sherlock.game.challenge.service;

import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.ChallengeSummary;
import com.sherlock.game.challenge.exception.ChallengeRoomNotFoundException;
import com.sherlock.game.challenge.exception.ChallengeSummaryNotFoundException;
import com.sherlock.game.challenge.repository.ChallengeRoomRepository;
import com.sherlock.game.challenge.repository.ChallengeSummaryRepository;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.exception.PlayerNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
        String gameId = generateRandomCode(20);
        ChallengeRoom room = ChallengeRoom.builder()
                .gameId(gameId)
                .gameConfig(config)
                .build();

        return challengeRoomMap.putIfAbsent(gameId, challengeRoomRepository.save(room));
    }

    @Override
    public Player getPlayer(String gameId, String playerName) {

        Assert.notNull(gameId, "Game id is required");
        Assert.notNull(playerName, "Player name is required");

        ChallengeRoom room = getRoom(gameId);
        return Optional.ofNullable(room.getPlayers())
                .map(players ->
                        players.stream()
                                .filter(p -> playerName.equalsIgnoreCase(p.getName()))
                                .findFirst()
                                .orElseThrow(PlayerNotFoundException::new))
                .orElseThrow(PlayerNotFoundException::new);
    }

    @Override
    public ChallengeSummary getSummary(String gameId) {

        Assert.notNull(gameId, "Game id is required");
        return challengeSummaryRepository.findById(gameId)
                .orElseThrow(ChallengeSummaryNotFoundException::new);
    }

    @Override
    public ChallengeRoom getRoom(String gameId) {

        Assert.notNull(gameId, "Game id is required");
        return Optional.ofNullable(challengeRoomMap.get(gameId))
                .orElseThrow(ChallengeRoomNotFoundException::new);
    }
}
