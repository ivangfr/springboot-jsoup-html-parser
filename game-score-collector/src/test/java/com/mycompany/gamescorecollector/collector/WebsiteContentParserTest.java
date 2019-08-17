package com.mycompany.gamescorecollector.collector;

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

class WebsiteContentParserTest {

    private static WebsiteContentParser contentParser;

    @BeforeAll
    static void setupBeforeAll() {
        contentParser = new WebsiteContentParser();
    }

    @Test
    void testDocumentNull() {
        List<GameScore> gameScores = contentParser.parse(null);
        assertEquals(0, gameScores.size());
    }

    @Test
    void testDocumentWithValidContent() throws IOException {
        File file = ResourceUtils.getFile("classpath:valid-content.html");
        Document document = Jsoup.parse(file, "UTF-8");
        List<GameScore> gameScores = contentParser.parse(document);
        assertNotNull(gameScores);
        assertEquals(3, gameScores.size());
        assertEquals("Red Dead Redemption 2", gameScores.get(0).getTitle());
        assertEquals(97, gameScores.get(0).getScore());
        assertEquals("Grand Theft Auto V", gameScores.get(1).getTitle());
        assertEquals(97, gameScores.get(1).getScore());
        assertEquals("Death Mark", gameScores.get(2).getTitle());
        assertEquals(83, gameScores.get(2).getScore());
    }

    @Test
    void testDocumentWithEmptyContent() throws IOException {
        File file = ResourceUtils.getFile("classpath:empty-content.html");
        Document document = Jsoup.parse(file, "UTF-8");
        List<GameScore> gameScores = contentParser.parse(document);
        assertEquals(0, gameScores.size());
    }

}