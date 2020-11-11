package com.sherlock.game.support;

import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;

public interface MessageProcessor {

    Subject getSubject();

    Envelop process(ChallengeRoom room, Player player, Envelop envelop);
}
