package com.mycompany.gamescoreapi.service;

import com.mycompany.gamescoreapi.exception.GameScoreNotFoundException;
import com.mycompany.gamescoreapi.model.GameScore;
import com.mycompany.gamescoreapi.repository.GameScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GameScoreServiceImpl implements GameScoreService {

    private final GameScoreRepository gameScoreRepository;

    @Override
    public GameScore getGameScore(Long id) {
        return gameScoreRepository.findById(id).orElseThrow(() -> new GameScoreNotFoundException(id));
    }

    @Override
    public Page<GameScore> getGameScores(Pageable pageable) {
        return gameScoreRepository.findAll(pageable);
    }

    @Override
    public Page<GameScore> getGameScores(Pageable pageable, String title) {
        return gameScoreRepository.findByTitleContainingIgnoreCase(pageable, title);
    }
}
