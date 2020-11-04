package com.sherlock.game.challenge.repository;

import com.sherlock.game.challenge.domain.ChallengeRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRoomRepository extends MongoRepository<ChallengeRoom, String> {
}
