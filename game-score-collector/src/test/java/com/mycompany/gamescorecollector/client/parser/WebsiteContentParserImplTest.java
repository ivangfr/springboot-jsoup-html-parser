package com.mycompany.gamescorecollector.client.parser;

import com.mycompany.gamescorecollector.model.GameScore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebsiteContentParserImplTest {

    private static WebsiteContentParser contentParser;

    @BeforeAll
    static void setupBeforeAll() {
        contentParser = new WebsiteContentParserImpl();
    }

    @Test
    void testDocumentNull() {
        Document document = null;
        List<GameScore> gameScores = contentParser.parse(document);
        assertEquals(gameScores.size(), 0);
    }

    @Test
    void testDocumentWithValidContent() throws IOException {
        File file = ResourceUtils.getFile("classpath:valid-content.html");
        Document document = Jsoup.parse(file, "UTF-8");
        List<GameScore> gameScores = contentParser.parse(document);
        assertNotNull(gameScores);
        assertEquals(gameScores.size(), 3);
        assertEquals(gameScores.get(0).getTitle(), "Red Dead Redemption 2");
        assertEquals(gameScores.get(0).getScore(), 97);
        assertEquals(gameScores.get(1).getTitle(), "Grand Theft Auto V");
        assertEquals(gameScores.get(1).getScore(), 97);
        assertEquals(gameScores.get(2).getTitle(), "Death Mark");
        assertEquals(gameScores.get(2).getScore(), 83);
    }

    @Test
    void testDocumentWithEmptyContent() throws IOException {
        File file = ResourceUtils.getFile("classpath:empty-content.html");
        Document document = Jsoup.parse(file, "UTF-8");
        List<GameScore> gameScores = contentParser.parse(document);
        assertEquals(gameScores.size(), 0);
    }

}