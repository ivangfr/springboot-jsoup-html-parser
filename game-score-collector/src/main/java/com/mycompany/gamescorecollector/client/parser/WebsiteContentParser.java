package com.mycompany.gamescorecollector.client.parser;

import com.mycompany.gamescorecollector.model.GameScore;
import org.jsoup.nodes.Document;

import java.util.List;

public interface WebsiteContentParser {

    List<GameScore> parse(Document document);

}
