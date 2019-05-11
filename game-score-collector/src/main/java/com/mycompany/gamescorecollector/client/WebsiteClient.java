package com.mycompany.gamescorecollector.client;

import com.mycompany.gamescorecollector.model.GameScore;

import java.util.List;

public interface WebsiteClient {

    List<GameScore> getGameScores();

}
