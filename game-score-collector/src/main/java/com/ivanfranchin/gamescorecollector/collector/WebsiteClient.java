package com.ivanfranchin.gamescorecollector.collector;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
public class WebsiteClient {

    public Document call(String url) {
        try {
            Instant start = Instant.now();
            log.info("Calling url {}", url);
            Document document = Jsoup.connect(url).get();
            log.info("Got document from url {} in {} ms", url, Duration.between(start, Instant.now()).toMillis());
            return document;
        } catch (IOException e) {
            log.error("Unable to get games scores from {}. Error message: {}", url, e.getMessage());
            return null;
        }
    }
}
