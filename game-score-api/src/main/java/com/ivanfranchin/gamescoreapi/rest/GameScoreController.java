package com.ivanfranchin.gamescoreapi.rest;

import com.ivanfranchin.gamescoreapi.rest.dto.GameScoreResponse;
import com.ivanfranchin.gamescoreapi.mapper.GameScoreMapper;
import com.ivanfranchin.gamescoreapi.model.GameScore;
import com.ivanfranchin.gamescoreapi.service.GameScoreService;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
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
    private final GameScoreMapper gameScoreMapper;

    @GetMapping
    public Page<GameScoreResponse> getGameScores(
            @RequestParam(required = false) String title, @ParameterObject Pageable pageable) {
        Page<GameScore> gameScorePage = title == null ?
                gameScoreService.getGameScores(pageable) : gameScoreService.getGameScores(pageable, title);
        return gameScorePage.map(gameScoreMapper::toGameScoreResponse);
    }

    @GetMapping("/{id}")
    public GameScoreResponse getGameScore(@PathVariable Long id) {
        GameScore gameScore = gameScoreService.getGameScore(id);
        return gameScoreMapper.toGameScoreResponse(gameScore);
    }
}
