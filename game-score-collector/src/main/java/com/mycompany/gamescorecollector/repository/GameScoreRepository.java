package com.mycompany.gamescorecollector.repository;

import com.mycompany.gamescorecollector.model.GameScore;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameScoreRepository extends MongoRepository<GameScore, Long> {
}
