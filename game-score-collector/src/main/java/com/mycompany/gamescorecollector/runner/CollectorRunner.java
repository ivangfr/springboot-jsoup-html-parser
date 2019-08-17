package com.mycompany.gamescorecollector.runner;

import com.mycompany.gamescorecollector.collector.WebsiteCollector;
import com.mycompany.gamescorecollector.model.GameScore;
import com.mycompany.gamescorecollector.service.GameScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CollectorRunner implements CommandLineRunner {

    private final GameScoreService gameScoreService;
    private final WebsiteCollector websiteCollector;

    @Override
    public void run(String... args) {
        log.info("Starting collecting game score data from website...");
        Instant start = Instant.now();

        List<GameScore> gameScores = websiteCollector.getGameScores();
        gameScoreService.saveGameScores(gameScores);

        Duration duration = Duration.between(start, Instant.now());
        log.info("Collected successfully {} game score data! Execution time: {}", gameScores.size(), duration.toMillis());
        log.info("First game = {}, Last game  = {}", gameScores.get(0), gameScores.get(gameScores.size() - 1));
    }

}
