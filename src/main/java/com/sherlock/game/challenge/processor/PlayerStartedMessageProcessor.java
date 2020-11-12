package com.sherlock.game.challenge.processor;

import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import com.sherlock.game.support.MessageProcessor;
import org.springframework.stereotype.Component;

import static com.sherlock.game.core.domain.message.Subject.PLAYER_STARTED;

@Component
public class PlayerStartedMessageProcessor implements MessageProcessor {

    @Override
    public Subject getSubject() {
        return PLAYER_STARTED;
    }

    @Override
    public Envelop process(ChallengeRoom room, Player player, Envelop envelop) {
        return room.broadcast(envelop);
    }
}
