package com.mycompany.gamescoreapi.repository;

import com.mycompany.gamescoreapi.model.GameScore;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameScoreRepository extends MongoRepository<GameScore, String> {
}
