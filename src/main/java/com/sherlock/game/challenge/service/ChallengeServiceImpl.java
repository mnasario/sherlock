package com.sherlock.game.challenge.service;

import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.ChallengeSummary;
import com.sherlock.game.core.domain.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChallengeServiceImpl implements ChallengeService {


    @Override
    public ChallengeRoom insert(ChallengeConfig config) {
        return null;
    }

    @Override
    public Player getPlayer(String gameId, String playerName) {
        return null;
    }

    @Override
    public ChallengeSummary getSummary(String gameId) {
        return null;
    }

    @Override
    public ChallengeRoom getRoom(String gameId) {
        return null;
    }
}
