package com.mycompany.gamescorecollector.repository;

import com.mycompany.gamescorecollector.model.GameScore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameScoreRepository extends MongoRepository<GameScore, Long> {
}
