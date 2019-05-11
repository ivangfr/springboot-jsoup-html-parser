package com.mycompany.gamescorecollector.client;

import com.mycompany.gamescorecollector.client.callable.WebsiteCallable;
import com.mycompany.gamescorecollector.client.parser.WebsiteContentParser;
import com.mycompany.gamescorecollector.model.GameScore;
import com.mycompany.gamescorecollector.client.properties.WebsiteProperties;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
@EnableConfigurationProperties(WebsiteProperties.class)
public class WebsiteClientImpl implements WebsiteClient {

    private final WebsiteProperties websiteProperties;
    private final WebsiteContentParser websiteContentParser;

    public WebsiteClientImpl(WebsiteProperties websiteProperties, WebsiteContentParser websiteContentParser) {
        this.websiteProperties = websiteProperties;
        this.websiteContentParser = websiteContentParser;
    }

    @Override
    public List<GameScore> getGameScores() {
        ExecutorService executorService = Executors.newFixedThreadPool(getNumThreads());

        List<WebsiteCallable> futureList = new ArrayList<>();
        for (int i = websiteProperties.getMinPage(); i <= websiteProperties.getMaxPage(); i++) {
            futureList.add(new WebsiteCallable(getUrl(i)));
        }

        List<GameScore> gameScores = new ArrayList<>();
        try {
            List<Future<Document>> futures = executorService.invokeAll(futureList);

            for (Future<Document> future : futures) {
                Document document = future.get();
                gameScores.addAll(websiteContentParser.parse(document));
            }
        } catch (Exception e) {
            log.error("An exception occurred while getting games scores.", e);
        } finally {
            executorService.shutdown();
        }

        return gameScores;
    }

    private int getNumThreads() {
        int numThreads = Runtime.getRuntime().availableProcessors();
        if (websiteProperties.getNumThreads() > 0) {
            numThreads = websiteProperties.getNumThreads();
        }
        log.info("{} thread(s) set for executor service", numThreads);
        return numThreads;
    }

    private String getUrl(int pageNum) {
        return String.format("%s?page=%d", websiteProperties.getUrl(), pageNum);
    }

}
