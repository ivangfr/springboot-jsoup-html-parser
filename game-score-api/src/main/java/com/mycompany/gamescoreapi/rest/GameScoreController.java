package com.mycompany.gamescoreapi.rest;

import com.mycompany.gamescoreapi.model.GameScore;
import com.mycompany.gamescoreapi.rest.dto.GameScoreDto;
import com.mycompany.gamescoreapi.service.GameScoreService;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GameScoreController {

    private final GameScoreService gameScoreService;
    private final MapperFacade mapperFacade;

    public GameScoreController(GameScoreService gameScoreService, MapperFacade mapperFacade) {
        this.gameScoreService = gameScoreService;
        this.mapperFacade = mapperFacade;
    }

    @ApiOperation(
            value = "Get all Game Score with pagination",
            notes = "To sort the results by a specified field, use in 'sort' field a string like: fieldname,[asc|desc]")
    @GetMapping("/games")
    public Page<GameScoreDto> getAllGameScoreByPage(Pageable pageable) {
        return gameScoreService.getAllGameScoreByPage(pageable).map(gameScore -> mapperFacade.map(gameScore, GameScoreDto.class));
    }

    @ApiOperation("Get Game Score filter by Title")
    @GetMapping("/games/{title}")
    public GameScoreDto getGameScoreByTitle(@PathVariable String title) {
        GameScore gameScore = gameScoreService.getGameScoreByTitle(title.trim());
        return mapperFacade.map(gameScore, GameScoreDto.class);
    }

}
