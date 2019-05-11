package com.mycompany.gamescoreapi.controller;

import com.mycompany.gamescoreapi.model.GameScore;
import com.mycompany.gamescoreapi.service.GameScoreService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameScoreController {

    private final GameScoreService gameScoreService;

    public GameScoreController(GameScoreService gameScoreService) {
        this.gameScoreService = gameScoreService;
    }

    @ApiOperation("Get all Game Score")
    @GetMapping("/games")
    public List<GameScore> getAllGameScore() {
        return gameScoreService.getAllGameScore();
    }

    @ApiOperation(
            value = "Get all Game Score with pagination",
            notes = "To sort the results by a specified field, use in 'sort' field a string like: fieldname,[asc|desc]")
    @GetMapping("/pg_games")
    public Page<GameScore> getAllGameScoreByPage(Pageable pageable) {
        return gameScoreService.getAllGameScoreByPage(pageable);
    }

    @ApiOperation("Get Game Score filter by Title")
    @GetMapping("/games/{title}")
    public GameScore getGameScoreByTitle(@PathVariable String title) {
        return gameScoreService.getGameScoreByTitle(title.trim());
    }

}
