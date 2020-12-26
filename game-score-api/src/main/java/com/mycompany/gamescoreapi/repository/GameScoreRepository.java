package com.mycompany.gamescoreapi.repository;

import com.mycompany.gamescoreapi.model.GameScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameScoreRepository extends MongoRepository<GameScore, Long> {

    Page<GameScore> findByTitleContainingIgnoreCase(Pageable pageable, String title);

}
