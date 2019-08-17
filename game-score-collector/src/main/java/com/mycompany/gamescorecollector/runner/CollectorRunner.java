package com.mycompany.gamescorecollector.runner;

import com.mycompany.gamescorecollector.collector.WebsiteCollector;
import com.mycompany.gamescorecollector.model.GameScore;
import com.mycompany.gamescorecollector.properties.CollectorProperties;
import com.mycompany.gamescorecollector.service.GameScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableConfigurationProperties(CollectorProperties.class)
public class CollectorRunner implements CommandLineRunner {

    private final GameScoreService gameScoreService;
    private final WebsiteCollector websiteCollector;
    private final CollectorProperties collectorProperties;

    @Override
    public void run(String... args) {
        log.info("Starting collecting game score data from website...");
        Instant start = Instant.now();

        int numGameScores = 0;
        switch (collectorProperties.getMode()) {
            case GET_AND_SAVE:
                numGameScores = websiteCollector.collectGameScores();
                break;

            case GET_ALL_AND_SAVE_ALL:
                List<GameScore> gameScores = websiteCollector.getGameScores();
                gameScoreService.saveGameScores(gameScores);
                numGameScores = gameScores.size();
                break;

            default:
                throw new IllegalArgumentException(
                        String.format("The collector mode '%s' is not valid.", collectorProperties.getMode().name()));
        }

        Duration duration = Duration.between(start, Instant.now());
        log.info("Collected successfully {} game score data! Execution time: {}", numGameScores, duration.toMillis());
    }

}
