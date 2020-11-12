package com.sherlock.game.challenge.service;

import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.ChallengeSummary;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;

import javax.websocket.Session;

public interface ChallengeService {

    ChallengeRoom insert(ChallengeConfig config);

    ChallengeRoom getRoom(String gameId);

    ChallengeSummary getSummary(String gameId);

    Player getPlayer(String gameId, String playerName);

    Envelop login(Session session, String gameId, String playerName);

    Envelop processMessage(Session session, String gameId, String playerName, Envelop message);

    Envelop summarize(Session session, String gameId, String playerName);

    Envelop processError(Session session, Throwable throwable);
}
