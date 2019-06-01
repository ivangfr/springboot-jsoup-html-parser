package com.mycompany.gamescoreapi.service;

import com.mycompany.gamescoreapi.exception.GameScoreNotFoundException;
import com.mycompany.gamescoreapi.model.GameScore;
import com.mycompany.gamescoreapi.repository.GameScoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GameScoreServiceImpl implements GameScoreService {

    private final GameScoreRepository gameScoreRepository;

    public GameScoreServiceImpl(GameScoreRepository gameScoreRepository) {
        this.gameScoreRepository = gameScoreRepository;
    }

    @Override
    public Page<GameScore> getAllGameScoreByPage(Pageable pageable) {
        return gameScoreRepository.findAll(pageable);
    }

    @Override
    public GameScore getGameScoreByTitle(String title) {
        return gameScoreRepository.findById(title)
                .orElseThrow(() -> new GameScoreNotFoundException(String.format("Game with title '%s' not found", title)));
    }

}
