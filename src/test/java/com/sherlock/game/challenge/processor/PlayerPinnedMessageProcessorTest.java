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
        Marker pinnedMarker = Marker.builder().latitude(10D).longitude(10D).build();
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

    @Test
    public void shouldCalculateScoreSuccessToBetween50MAnd100M() throws JsonProcessingException {

        //given
        Marker pinnedMarker = Marker.builder().latitude(80D).longitude(80D).build();
        MarkerPin markerPinTo10M = buildMarkerPin(playerHost, markerB, pinnedMarker, 80D);
        markerPinnedMessage.setPayload(mapper.writeValueAsString(markerPinTo10M));
        requestDefault.setPlayer(playerHost);

        //when
        Envelop messageResponse = processor.process(requestDefault);

        //then
        Score scoreResponse = mapper.readValue(messageResponse.getPayload(), Score.class);
        Assert.assertNotNull(scoreResponse);
        Assert.assertEquals(Double.valueOf(99), scoreResponse.getScoreValue());
        Assert.assertFalse(playerHost.hasFinishedGame());
        Assert.assertNotNull(playerHost.getScores());
        Assert.assertFalse(playerHost.getScores().isEmpty());
        Assert.assertEquals(1, playerHost.getScores().size());
    }

    @Test
    public void shouldCalculateScoreSuccessToBetween100MAnd150M() throws JsonProcessingException {

        //given
        Marker pinnedMarker = Marker.builder().latitude(130D).longitude(130D).build();
        MarkerPin markerPinTo10M = buildMarkerPin(playerHost, markerB, pinnedMarker, 130D);
        markerPinnedMessage.setPayload(mapper.writeValueAsString(markerPinTo10M));
        requestDefault.setPlayer(playerHost);

        //when
        Envelop messageResponse = processor.process(requestDefault);

        //then
        Score scoreResponse = mapper.readValue(messageResponse.getPayload(), Score.class);
        Assert.assertNotNull(scoreResponse);
        Assert.assertEquals(Double.valueOf(98), scoreResponse.getScoreValue());
        Assert.assertFalse(playerHost.hasFinishedGame());
        Assert.assertNotNull(playerHost.getScores());
        Assert.assertFalse(playerHost.getScores().isEmpty());
        Assert.assertEquals(1, playerHost.getScores().size());
    }

    @Test
    public void shouldCalculateScoreSuccessToBetween150MAnd200M() throws JsonProcessingException {

        //given
        Marker pinnedMarker = Marker.builder().latitude(170D).longitude(170D).build();
        MarkerPin markerPinTo10M = buildMarkerPin(playerHost, markerB, pinnedMarker, 170D);
        markerPinnedMessage.setPayload(mapper.writeValueAsString(markerPinTo10M));
        requestDefault.setPlayer(playerHost);

        //when
        Envelop messageResponse = processor.process(requestDefault);

        //then
        Score scoreResponse = mapper.readValue(messageResponse.getPayload(), Score.class);
        Assert.assertNotNull(scoreResponse);
        Assert.assertEquals(Double.valueOf(97), scoreResponse.getScoreValue());
        Assert.assertFalse(playerHost.hasFinishedGame());
        Assert.assertNotNull(playerHost.getScores());
        Assert.assertFalse(playerHost.getScores().isEmpty());
        Assert.assertEquals(1, playerHost.getScores().size());
    }

    @Test
    public void shouldCalculateScoreSuccessToBetween200MAnd300M() throws JsonProcessingException {

        //given
        Marker pinnedMarker = Marker.builder().latitude(250D).longitude(250D).build();
        MarkerPin markerPinTo10M = buildMarkerPin(playerHost, markerB, pinnedMarker, 250D);
        markerPinnedMessage.setPayload(mapper.writeValueAsString(markerPinTo10M));
        requestDefault.setPlayer(playerHost);

        //when
        Envelop messageResponse = processor.process(requestDefault);

        //then
        Score scoreResponse = mapper.readValue(messageResponse.getPayload(), Score.class);
        Assert.assertNotNull(scoreResponse);
        Assert.assertEquals(Double.valueOf(96), scoreResponse.getScoreValue());
        Assert.assertFalse(playerHost.hasFinishedGame());
        Assert.assertNotNull(playerHost.getScores());
        Assert.assertFalse(playerHost.getScores().isEmpty());
        Assert.assertEquals(1, playerHost.getScores().size());
    }

    @Test
    public void shouldCalculateScoreSuccessToBetween5500MAnd8900M() throws JsonProcessingException {

        //given
        Marker pinnedMarker = Marker.builder().latitude(6900D).longitude(6900D).build();
        MarkerPin markerPinTo10M = buildMarkerPin(playerHost, markerB, pinnedMarker, 6900D);
        markerPinnedMessage.setPayload(mapper.writeValueAsString(markerPinTo10M));
        requestDefault.setPlayer(playerHost);

        //when
        Envelop messageResponse = processor.process(requestDefault);

        //then
        Score scoreResponse = mapper.readValue(messageResponse.getPayload(), Score.class);
        Assert.assertNotNull(scoreResponse);
        Assert.assertEquals(Double.valueOf(89), scoreResponse.getScoreValue());
        Assert.assertFalse(playerHost.hasFinishedGame());
        Assert.assertNotNull(playerHost.getScores());
        Assert.assertFalse(playerHost.getScores().isEmpty());
        Assert.assertEquals(1, playerHost.getScores().size());
    }

    @Test
    public void shouldCalculateScoreSuccessToBetween1000KMAnd10000KM() throws JsonProcessingException {

        //given
        Marker pinnedMarker = Marker.builder().latitude(5_000_000D).longitude(5_000_000D).build();
        MarkerPin markerPinTo10M = buildMarkerPin(playerHost, markerB, pinnedMarker, 5_000_000D);
        markerPinnedMessage.setPayload(mapper.writeValueAsString(markerPinTo10M));
        requestDefault.setPlayer(playerHost);

        //when
        Envelop messageResponse = processor.process(requestDefault);

        //then
        Score scoreResponse = mapper.readValue(messageResponse.getPayload(), Score.class);
        Assert.assertNotNull(scoreResponse);
        Assert.assertEquals(Double.valueOf(58), scoreResponse.getScoreValue());
        Assert.assertFalse(playerHost.hasFinishedGame());
        Assert.assertNotNull(playerHost.getScores());
        Assert.assertFalse(playerHost.getScores().isEmpty());
        Assert.assertEquals(1, playerHost.getScores().size());
    }

    @Test
    public void shouldCalculateScoreSuccessToBetween10000KMAnd14500KM() throws JsonProcessingException {

        //given
        Marker pinnedMarker = Marker.builder().latitude(12_000_000D).longitude(12_000_000D).build();
        MarkerPin markerPinTo10M = buildMarkerPin(playerHost, markerB, pinnedMarker, 12_000_000D);
        markerPinnedMessage.setPayload(mapper.writeValueAsString(markerPinTo10M));
        requestDefault.setPlayer(playerHost);

        //when
        Envelop messageResponse = processor.process(requestDefault);

        //then
        Score scoreResponse = mapper.readValue(messageResponse.getPayload(), Score.class);
        Assert.assertNotNull(scoreResponse);
        Assert.assertEquals(Double.valueOf(17), scoreResponse.getScoreValue());
        Assert.assertFalse(playerHost.hasFinishedGame());
        Assert.assertNotNull(playerHost.getScores());
        Assert.assertFalse(playerHost.getScores().isEmpty());
        Assert.assertEquals(1, playerHost.getScores().size());
    }

    @Test
    public void shouldCalculateScoreSuccessToMoreThan14500KM() throws JsonProcessingException {

        //given
        Marker pinnedMarker = Marker.builder().latitude(14_600_000D).longitude(14_600_000D).build();
        MarkerPin markerPinTo10M = buildMarkerPin(playerHost, markerB, pinnedMarker, 14_600_000D);
        markerPinnedMessage.setPayload(mapper.writeValueAsString(markerPinTo10M));
        requestDefault.setPlayer(playerHost);

        //when
        Envelop messageResponse = processor.process(requestDefault);

        //then
        Score scoreResponse = mapper.readValue(messageResponse.getPayload(), Score.class);
        Assert.assertNotNull(scoreResponse);
        Assert.assertEquals(Double.valueOf(0), scoreResponse.getScoreValue());
        Assert.assertFalse(playerHost.hasFinishedGame());
        Assert.assertNotNull(playerHost.getScores());
        Assert.assertFalse(playerHost.getScores().isEmpty());
        Assert.assertEquals(1, playerHost.getScores().size());
    }
}
