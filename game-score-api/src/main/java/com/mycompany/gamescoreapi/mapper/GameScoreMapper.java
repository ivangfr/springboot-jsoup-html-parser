package com.mycompany.gamescoreapi.mapper;

import com.mycompany.gamescoreapi.model.GameScore;
import com.mycompany.gamescoreapi.rest.dto.GameScoreResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameScoreMapper {

    GameScoreResponse toGameScoreResponse(GameScore gameScore);
}
