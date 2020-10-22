package com.sherlock.game.core.repository;

import com.sherlock.game.core.domain.Marker;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface MarkerRepository extends MongoRepository<Marker, UUID> {
}
