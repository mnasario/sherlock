package com.sherlock.game.challenge.repository;

import com.sherlock.game.challenge.domain.ChallengeSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeSummaryRepository extends MongoRepository<ChallengeSummary, String> {
}
