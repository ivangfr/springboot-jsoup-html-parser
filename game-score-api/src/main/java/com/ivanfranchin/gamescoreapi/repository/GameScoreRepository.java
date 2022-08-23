package com.ivanfranchin.gamescoreapi.repository;

import com.ivanfranchin.gamescoreapi.model.GameScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameScoreRepository extends MongoRepository<GameScore, Long> {

    Page<GameScore> findByTitleContainingIgnoreCase(Pageable pageable, String title);
}
