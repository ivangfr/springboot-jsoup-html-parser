package com.mycompany.gamescoreapi.mapper;

import com.mycompany.gamescoreapi.model.GameScore;
import com.mycompany.gamescoreapi.rest.dto.GameScoreDto;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Configuration;

@Configuration
@Mapper(componentModel = "spring")
public interface GameScoreMapper {

    GameScoreDto toGameScoreDto(GameScore gameScore);

}
