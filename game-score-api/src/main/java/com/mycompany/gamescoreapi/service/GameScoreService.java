package com.mycompany.gamescoreapi.service;

import com.mycompany.gamescoreapi.model.GameScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GameScoreService {

    List<GameScore> getAllGameScore();

    Page<GameScore> getAllGameScoreByPage(Pageable pageable);

    GameScore getGameScoreByTitle(String title);

}
