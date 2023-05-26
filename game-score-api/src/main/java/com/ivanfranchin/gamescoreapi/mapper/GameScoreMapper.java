package com.ivanfranchin.gamescoreapi.mapper;

import com.ivanfranchin.gamescoreapi.model.GameScore;
import com.ivanfranchin.gamescoreapi.rest.dto.GameScoreResponse;

public interface GameScoreMapper {

    GameScoreResponse toGameScoreResponse(GameScore gameScore);
}
