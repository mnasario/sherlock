package com.sherlock.game.challenge.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.MessageRequest;
import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.domain.MarkerPin;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.Score;
import com.sherlock.game.core.domain.message.Envelop;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.websocket.Session;
import java.util.Map;
import java.util.UUID;

import static com.sherlock.game.core.domain.message.Subject.PLAYER_PINNED;
import static com.sherlock.game.core.domain.message.Type.SYSTEM;
import static com.sherlock.game.support.GameModelFactory.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerPinnedMessageProcessorTest {

    @Mock
    private Session sessionJoao;

    @Mock
    private Session sessionJose;

    private ObjectMapper mapper = buildMapper();
    private PlayerPinnedMessageProcessor processor = new PlayerPinnedMessageProcessor(mapper);

    private Player playerHost;
    private Player playerJose;
    private String gameId = "GAME_1";
    private Marker markerA;
    private Marker markerB;
    private ChallengeConfig configDefault;
    private Map<String, Player> playersMap;
    private ChallengeRoom room;
    private Envelop markerPinnedMessage;
    private MessageRequest requestDefault;

    @Before
    public void setUp() {

        playerHost = buildPlayer("João", sessionJoao);
        playerJose = buildPlayer("José", sessionJose);
        playersMap = buildPlayersMap(playerHost, playerJose);
        markerPinnedMessage = buildMessage(SYSTEM, PLAYER_PINNED);
        markerA = buildMarker(UUID.randomUUID(), 10D, 10D);
        markerB = buildMarker(UUID.randomUUID(), 11D, 11D);

        configDefault = buildChallengeConfig(
                playerHost,
                90L,
                3,
                markerA,
                markerB);

        room = buildChallengeRoom(gameId, configDefault, playersMap);
        requestDefault = MessageRequest.builder()
                .room(room)
                .envelop(markerPinnedMessage)
                .build();
    }

    @Test
    public void shouldCalculateScoreSuccessToLess50M() throws JsonProcessingException {

        //given
        Marker pinnedMarker = Marker.builder().latitude(20D).longitude(20D).build();
        MarkerPin markerPinTo10M = buildMarkerPin(playerJose, markerA, pinnedMarker, 10D);
        markerPinnedMessage.setPayload(mapper.writeValueAsString(markerPinTo10M));
        requestDefault.setPlayer(playerJose);

        //when
        Envelop messageResponse = processor.process(requestDefault);

        //then
        Score scoreResponse = mapper.readValue(messageResponse.getPayload(), Score.class);
        Assert.assertNotNull(scoreResponse);
        Assert.assertEquals(Double.valueOf(100), scoreResponse.getScoreValue());
        Assert.assertFalse(playerJose.hasFinishedGame());
        Assert.assertNotNull(playerJose.getScores());
        Assert.assertFalse(playerJose.getScores().isEmpty());
        Assert.assertEquals(1, playerJose.getScores().size());
    }
}
