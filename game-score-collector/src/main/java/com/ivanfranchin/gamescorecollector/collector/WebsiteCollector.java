package com.ivanfranchin.gamescorecollector.collector;

import com.ivanfranchin.gamescorecollector.properties.CollectorProperties;
import com.ivanfranchin.gamescorecollector.service.GameScoreService;
import com.ivanfranchin.gamescorecollector.model.GameScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebsiteCollector {

    private final CollectorProperties collectorProperties;
    private final WebsiteClient websiteClient;
    private final WebsiteContentParser websiteContentParser;
    private final GameScoreService gameScoreService;

    public int collectGameScores() {
        int minPage = collectorProperties.getWebsite().getMinPage();
        int maxPage = collectorProperties.getWebsite().getMaxPage();
        LongAccumulator longAccumulator = new LongAccumulator(Long::sum, 0);

        CompletableFuture.allOf(IntStream
                .range(minPage, maxPage + 1)
                .mapToObj(i ->
                        CompletableFuture.supplyAsync(() -> websiteClient.call(getPageUrl(i)))
                                .thenApply(websiteContentParser::parse)
                                .thenAccept(gameScores -> {
                                    longAccumulator.accumulate(gameScores.size());
                                    gameScoreService.saveGameScores(gameScores);
                                }))
                .toArray(CompletableFuture[]::new)
        ).join();

        return longAccumulator.intValue();
    }

    public List<GameScore> getGameScores() {
        int minPage = collectorProperties.getWebsite().getMinPage();
        int maxPage = collectorProperties.getWebsite().getMaxPage();

        List<CompletableFuture<List<GameScore>>> completableFutureList = IntStream
                .range(minPage, maxPage + 1)
                .mapToObj(i ->
                        CompletableFuture.supplyAsync(() ->
                                websiteClient.call(getPageUrl(i))).thenApply(websiteContentParser::parse))
                .toList();

        return CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0]))
                .thenApply(v ->
                        completableFutureList.stream()
                                .map(CompletableFuture::join)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList()))
                .join();
    }

    private String getPageUrl(int pageNum) {
        String url = collectorProperties.getWebsite().getUrl();
        return String.format("%s?page=%d", url, pageNum);
    }
}
