package com.ivanfranchin.gamescorecollector.service;

import com.ivanfranchin.gamescorecollector.model.GameScore;

import java.util.List;

public interface GameScoreService {

    List<GameScore> saveGameScores(List<GameScore> gameScores);
}
