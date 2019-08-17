package com.mycompany.gamescorecollector.collector;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class WebsiteClient {

    public Document call(String url) {
        Document document = null;
        try {
            log.info("Calling url {}", url);
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("Unable to get games scores from {}", url);
        }
        return document;
    }

}
