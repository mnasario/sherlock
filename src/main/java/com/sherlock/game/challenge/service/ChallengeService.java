package com.sherlock.game.challenge.service;

import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.ChallengeSummary;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;

public interface ChallengeService {

    ChallengeRoom insert(ChallengeConfig config);

    ChallengeRoom getRoom(String gameId);

    ChallengeSummary getSummary(String gameId);

    Player getPlayer(String gameId, String playerName);

    Envelop login(String gameId, Player player);

    Envelop processMessage(String gameId, Envelop message, String playerName);

    Envelop summarize(String gameId, String playerName);

    Envelop processError(String gameId, String playerName, Throwable throwable);
}
