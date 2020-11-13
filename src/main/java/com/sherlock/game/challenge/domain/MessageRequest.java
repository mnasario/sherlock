package com.sherlock.game.challenge.domain;

import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageRequest {

    private ChallengeRoom room;
    private Player player;
    private Envelop envelop;
}
