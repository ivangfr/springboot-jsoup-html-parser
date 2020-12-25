package com.mycompany.gamescorecollector.collector;

import com.mycompany.gamescorecollector.model.GameScore;
import com.mycompany.gamescorecollector.properties.CollectorProperties;
import com.mycompany.gamescorecollector.service.GameScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebsiteCollector {

    @Value("${collector.website.min-page}")
    private int minPage;

    @Value("${collector.website.max-page}")
    private int maxPage;

    @Value("${collector.website.url}")
    private String url;

    private final CollectorProperties websiteProperties;
    private final WebsiteClient websiteClient;
    private final WebsiteContentParser websiteContentParser;
    private final GameScoreService gameScoreService;

    public int collectGameScores() {
        LongAccumulator longAccumulator = new LongAccumulator(Long::sum, 0);
        List<CompletableFuture<Void>> completableFutureList = new ArrayList<>();
        for (int i = minPage; i <= maxPage; i++) {
            String pageUrl = getPageUrl(i);
            completableFutureList.add(
                    CompletableFuture.supplyAsync(() -> websiteClient.call(pageUrl))
                            .thenApply(websiteContentParser::parse)
                            .thenAccept(gameScores -> {
                                longAccumulator.accumulate(gameScores.size());
                                gameScoreService.saveGameScores(gameScores);
                            })
            );
        }

        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0])).join();

        return longAccumulator.intValue();
    }

    public List<GameScore> getGameScores() {
        List<CompletableFuture<List<GameScore>>> completableFutureList = new ArrayList<>();
        for (int i = minPage; i <= maxPage; i++) {
            String pageUrl = getPageUrl(i);
            completableFutureList.add(
                    CompletableFuture.supplyAsync(() -> websiteClient.call(pageUrl))
                            .thenApply(websiteContentParser::parse)
            );
        }

        return CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0]))
                .thenApply(v ->
                        completableFutureList.stream()
                                .map(CompletableFuture::join)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList()))
                .join();
    }

    private String getPageUrl(int pageNum) {
        return String.format("%s?page=%d", url, pageNum);
    }

}
