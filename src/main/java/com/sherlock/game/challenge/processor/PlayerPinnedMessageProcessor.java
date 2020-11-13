package com.sherlock.game.challenge.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.MessageRequest;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.Score;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static com.sherlock.game.core.domain.message.Subject.GAME_FINISHED;
import static com.sherlock.game.core.domain.message.Subject.PLAYER_PINNED;
import static com.sherlock.game.core.domain.message.Type.INFO;
import static com.sherlock.game.core.domain.message.Type.SYSTEM;

@Component
@AllArgsConstructor
public class PlayerPinnedMessageProcessor implements ChallengeMessageProcessor {

    private final ObjectMapper mapper;

    @Override
    public Subject getSubject() {
        return PLAYER_PINNED;
    }

    @Override
    public Envelop process(MessageRequest messageRequest) {

        ChallengeRoom room = messageRequest.getRoom();
        Player player = messageRequest.getPlayer();

        Score score = Score.builder().build();
        //TODO calcular o score do jogador aqui

        if (player.hasFinishedGame()) {
            player.send(SYSTEM, GAME_FINISHED, score, mapper);
            return room.broadcast(SYSTEM, GAME_FINISHED, player);
        }

        player.send(SYSTEM, PLAYER_PINNED, score, mapper);
        return room.broadcast(INFO, PLAYER_PINNED, player);
    }
}
