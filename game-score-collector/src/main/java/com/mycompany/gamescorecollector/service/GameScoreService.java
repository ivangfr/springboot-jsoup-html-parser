package com.mycompany.gamescorecollector.service;

import com.mycompany.gamescorecollector.model.GameScore;

import java.util.List;

public interface GameScoreService {

    List<GameScore> saveGameScores(List<GameScore> gameScores);
}
