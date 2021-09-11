package com.mycompany.gamescorecollector.service;

import com.mycompany.gamescorecollector.model.GameScore;
import com.mycompany.gamescorecollector.repository.GameScoreRepository;
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
