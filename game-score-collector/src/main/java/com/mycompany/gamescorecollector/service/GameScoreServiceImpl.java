package com.mycompany.gamescorecollector.service;

import com.mycompany.gamescorecollector.model.GameScore;
import com.mycompany.gamescorecollector.repository.GameScoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameScoreServiceImpl implements GameScoreService {

    private final GameScoreRepository gameScoreRepository;

    public GameScoreServiceImpl(GameScoreRepository gameScoreRepository) {
        this.gameScoreRepository = gameScoreRepository;
    }

    @Override
    public List<GameScore> saveGameScores(List<GameScore> gameScores) {
        return gameScoreRepository.saveAll(gameScores);
    }

}
