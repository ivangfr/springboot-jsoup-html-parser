package com.mycompany.gamescoreapi.service;

import com.mycompany.gamescoreapi.model.GameScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameScoreService {

    Page<GameScore> getAllGameScoreByPage(Pageable pageable);

    GameScore getGameScoreByTitle(String title);

}
