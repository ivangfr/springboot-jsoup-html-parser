package com.ivanfranchin.gamescoreapi.service;

import com.ivanfranchin.gamescoreapi.model.GameScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameScoreService {

    GameScore getGameScore(Long id);

    Page<GameScore> getGameScores(Pageable pageable);

    Page<GameScore> getGameScores(Pageable pageable, String title);
}
