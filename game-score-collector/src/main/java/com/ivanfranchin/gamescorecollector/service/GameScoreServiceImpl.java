package com.ivanfranchin.gamescorecollector.service;

import com.ivanfranchin.gamescorecollector.repository.GameScoreRepository;
import com.ivanfranchin.gamescorecollector.model.GameScore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GameScoreServiceImpl implements GameScoreService {

    private final GameScoreRepository gameScoreRepository;

    @Override
    public List<GameScore> saveGameScores(List<GameScore> gameScores) {
        return gameScoreRepository.saveAll(gameScores);
    }
}
