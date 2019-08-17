package com.mycompany.gamescorecollector.collector;

import com.mycompany.gamescorecollector.model.GameScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableConfigurationProperties(WebsiteProperties.class)
public class WebsiteCollector {

    private final WebsiteProperties websiteProperties;
    private final WebsiteClient websiteClient;
    private final WebsiteContentParser websiteContentParser;

    public List<GameScore> getGameScores() {
        List<CompletableFuture<List<GameScore>>> completableFutureList = new ArrayList<>();
        for (int i = websiteProperties.getMinPage(); i <= websiteProperties.getMaxPage(); i++) {
            String url = getUrl(i);
            completableFutureList.add(
                    CompletableFuture.supplyAsync(() -> websiteClient.call(url)).thenApply(websiteContentParser::parse));
        }

        CompletableFuture<Void> allCompletableFuture = CompletableFuture.allOf(
                completableFutureList.toArray(new CompletableFuture[completableFutureList.size()]));

        CompletableFuture<List<GameScore>> completableFutureOfGameScoreList = allCompletableFuture.thenApply(v ->
                completableFutureList.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );

        List<GameScore> gameScores;
        try {
            gameScores = completableFutureOfGameScoreList.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("An exception occurred while getting games scores.", e);
            gameScores = Collections.emptyList();
            Thread.currentThread().interrupt();
        }
        return gameScores;
    }

    private String getUrl(int pageNum) {
        return String.format("%s?page=%d", websiteProperties.getUrl(), pageNum);
    }

    // Can be used in case of setting a specific ExecutorService in getGameScores method
    private int getNumThreads() {
        int numThreads = Runtime.getRuntime().availableProcessors();
        if (websiteProperties.getNumThreads() > 0) {
            numThreads = websiteProperties.getNumThreads();
        }
        log.info("{} thread(s) set for executor service", numThreads);
        return numThreads;
    }

}
