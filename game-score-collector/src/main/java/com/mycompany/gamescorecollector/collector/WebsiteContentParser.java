package com.mycompany.gamescorecollector.collector;

import com.mycompany.gamescorecollector.model.GameScore;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WebsiteContentParser {

    private static final String GAME_SELECTOR = "table.clamp-list > tbody > tr:not(.spacer)";
    private static final String GAME_TITLE_SELECTOR = "h3";
    private static final String GAME_SCORE_SELECTOR = ".clamp-score-wrap > a > .metascore_w";

    public List<GameScore> parse(Document document) {
        if (document == null) {
            return Collections.emptyList();
        }

        Elements elements = document.select(GAME_SELECTOR);
        return elements.stream().map(element -> {
            String title = element.select(GAME_TITLE_SELECTOR).text().trim();
            int score = Integer.parseInt(element.select(GAME_SCORE_SELECTOR).html());
            return new GameScore(title, score);
        }).collect(Collectors.toList());
    }

}
