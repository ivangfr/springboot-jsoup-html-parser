package com.ivanfranchin.gamescorecollector.runner;

import com.ivanfranchin.gamescorecollector.properties.CollectorProperties;
import com.ivanfranchin.gamescorecollector.collector.WebsiteCollector;
import com.ivanfranchin.gamescorecollector.model.GameScore;
import com.ivanfranchin.gamescorecollector.service.GameScoreService;
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

    private final CollectorProperties collectorProperties;
    private final GameScoreService gameScoreService;
    private final WebsiteCollector websiteCollector;

    @Override
    public void run(String... args) {
        CollectorProperties.Mode mode = collectorProperties.getMode();
        log.info("Starting collecting game score data from website. Mode selected: {}", mode.name());
        Instant start = Instant.now();

        int numGameScores;
        switch (mode) {
            case GET_AND_SAVE:
                numGameScores = websiteCollector.collectGameScores();
                break;

            case GET_ALL_AND_SAVE_ALL:
                List<GameScore> gameScores = websiteCollector.getGameScores();
                gameScoreService.saveGameScores(gameScores);
                numGameScores = gameScores.size();
                break;

            default:
                throw new IllegalArgumentException(String.format("The collector mode '%s' is not valid.", mode.name()));
        }

        Duration duration = Duration.between(start, Instant.now());
        log.info("Collected successfully {} game score data! Execution time: {}", numGameScores, duration.toMillis());
    }
}
