package com.mycompany.gamescoreapi.rest;

import com.mycompany.gamescoreapi.model.GameScore;
import com.mycompany.gamescoreapi.rest.dto.GameScoreDto;
import com.mycompany.gamescoreapi.service.GameScoreService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/games")
public class GameScoreController {

    private final GameScoreService gameScoreService;
    private final MapperFacade mapperFacade;

    @ApiOperation(
            value = "Get all game scores or filtered by title with pagination",
            notes = "To sort the results by a specified field, use in 'sort' field a string like: field name,[asc|desc]")
    @GetMapping
    public Page<GameScoreDto> getGameScores(Pageable pageable, @RequestParam(required = false) String title) {
        Page<GameScore> gameScorePage = title == null ?
                gameScoreService.getGameScores(pageable) : gameScoreService.getGameScores(pageable, title);
        return gameScorePage.map(gameScore -> mapperFacade.map(gameScore, GameScoreDto.class));
    }

    @ApiOperation("Get a specific game score filtered by id")
    @GetMapping("{id}")
    public GameScoreDto getGameScore(@PathVariable Long id) {
        GameScore gameScore = gameScoreService.getGameScore(id);
        return mapperFacade.map(gameScore, GameScoreDto.class);
    }

}
