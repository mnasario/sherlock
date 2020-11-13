package com.sherlock.game.challenge.processor;

import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.MessageRequest;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import org.springframework.stereotype.Component;

import static com.sherlock.game.core.domain.message.Subject.PLAYER_UNFOCUSED;
import static com.sherlock.game.core.domain.message.Type.INFO;

@Component
public class PlayerUnfocusedMessageProcessor implements ChallengeMessageProcessor {

    @Override
    public Subject getSubject() {
        return PLAYER_UNFOCUSED;
    }

    @Override
    public Envelop process(MessageRequest messageRequest) {

        ChallengeRoom room = messageRequest.getRoom();
        Player player = messageRequest.getPlayer();
        return room.broadcast(INFO, PLAYER_UNFOCUSED, player);
    }
}
