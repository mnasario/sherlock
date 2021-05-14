package com.sherlock.game.challenge.processor;

import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.ChallengeSummary;
import com.sherlock.game.challenge.domain.MessageRequest;
import com.sherlock.game.challenge.repository.ChallengeSummaryRepository;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.Score;
import com.sherlock.game.core.domain.ScoreSummary;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.sherlock.game.core.domain.message.Subject.GAME_SUMMARIZED;
import static com.sherlock.game.core.domain.message.Subject.PLAYER_LEFT;
import static com.sherlock.game.core.domain.message.Type.INFO;

@Slf4j
@Component
@AllArgsConstructor
public class GameSummarizedMessageProcessor implements ChallengeMessageProcessor {

    private final ChallengeSummaryRepository challengeSummaryRepository;

    @Override
    public Subject getSubject() {
        return GAME_SUMMARIZED;
    }

    @Override
    public Envelop process(MessageRequest messageRequest) {

        ChallengeRoom room = messageRequest.getRoom();
        Player player = messageRequest.getPlayer();

        ChallengeSummary summary = room.getSummary();
        summary.addScoreSummary(getScoreSummaryBy(player));
        challengeSummaryRepository.save(summary);
        return room.broadcast(INFO, PLAYER_LEFT, player);
    }

    private ScoreSummary getScoreSummaryBy(Player player) {

        Double totalScore = Optional.ofNullable(player.getScores())
                .map(scores -> scores.stream().mapToDouble(Score::getScoreValue).sum())
                .orElse(null);

        return Optional.ofNullable(totalScore)
                .map(total -> ScoreSummary.builder()
                        .player(player)
                        .totalScore(total.longValue())
                        .build())
                .orElse(null);
    }
}