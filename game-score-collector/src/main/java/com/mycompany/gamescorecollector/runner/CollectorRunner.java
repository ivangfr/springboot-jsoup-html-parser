package com.mycompany.gamescorecollector.runner;

import com.mycompany.gamescorecollector.client.WebsiteClient;
import com.mycompany.gamescorecollector.model.GameScore;
import com.mycompany.gamescorecollector.service.GameScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CollectorRunner implements CommandLineRunner {

    private final GameScoreService gameScoreService;
    private final WebsiteClient websiteClient;

    public CollectorRunner(GameScoreService gameScoreService, WebsiteClient websiteClient) {
        this.gameScoreService = gameScoreService;
        this.websiteClient = websiteClient;
    }

    @Override
    public void run(String... args) {
        log.info("Starting collecting game score data from website...");

        List<GameScore> gameScores = websiteClient.getGameScores();
        gameScoreService.saveGameScores(gameScores);

        log.info("First game = {}, Last game  = {}", gameScores.get(0), gameScores.get(gameScores.size() - 1));
        log.info("Collected successfully {} game score data!", gameScores.size());
    }

}
