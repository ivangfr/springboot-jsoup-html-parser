package com.mycompany.gamescoreapi.rest;

import com.mycompany.gamescoreapi.mapper.GameScoreMapper;
import com.mycompany.gamescoreapi.model.GameScore;
import com.mycompany.gamescoreapi.rest.dto.GameScoreDto;
import com.mycompany.gamescoreapi.service.GameScoreService;
import lombok.RequiredArgsConstructor;
//import org.springdoc.api.annotations.ParameterObject;
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
    // -- It's commented for now as it's not working with spring-native
    //public Page<GameScoreDto> getGameScores(@RequestParam(required = false) String title, @ParameterObject Pageable pageable) {
    public Page<GameScoreDto> getGameScores(@RequestParam(required = false) String title, Pageable pageable) {
        Page<GameScore> gameScorePage = title == null ?
                gameScoreService.getGameScores(pageable) : gameScoreService.getGameScores(pageable, title);
        return gameScorePage.map(gameScoreMapper::toGameScoreDto);
    }

    @GetMapping("{id}")
    public GameScoreDto getGameScore(@PathVariable Long id) {
        GameScore gameScore = gameScoreService.getGameScore(id);
        return gameScoreMapper.toGameScoreDto(gameScore);
    }

}
