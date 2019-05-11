package com.mycompany.gamescorecollector.client.callable;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.Callable;

@Slf4j
public class WebsiteCallable implements Callable<Document> {

    private String url;

    public WebsiteCallable(String url) {
        this.url = url;
    }

    @Override
    public Document call() {
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
