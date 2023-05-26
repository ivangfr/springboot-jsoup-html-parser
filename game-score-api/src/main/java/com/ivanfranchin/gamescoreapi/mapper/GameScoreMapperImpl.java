package com.ivanfranchin.gamescoreapi.mapper;

import com.ivanfranchin.gamescoreapi.model.GameScore;
import com.ivanfranchin.gamescoreapi.rest.dto.GameScoreResponse;
import org.springframework.stereotype.Service;

@Service
public class GameScoreMapperImpl implements GameScoreMapper {

    @Override
    public GameScoreResponse toGameScoreResponse(GameScore gameScore) {
        if (gameScore == null) {
            return null;
        }
        return new GameScoreResponse(gameScore.getId(), gameScore.getTitle(), gameScore.getScore());
    }
}
