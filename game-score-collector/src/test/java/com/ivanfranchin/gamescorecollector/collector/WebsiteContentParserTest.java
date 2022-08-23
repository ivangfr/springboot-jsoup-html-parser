package com.ivanfranchin.gamescorecollector.collector;

import com.ivanfranchin.gamescorecollector.model.GameScore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WebsiteContentParserTest {

    private static WebsiteContentParser contentParser;

    @BeforeAll
    static void setupBeforeAll() {
        contentParser = new WebsiteContentParser();
    }

    @Test
    void testDocumentNull() {
        List<GameScore> gameScores = contentParser.parse(null);

        assertThat(gameScores.size()).isEqualTo(0);
    }

    @Test
    void testDocumentWithValidContent() throws IOException {
        File file = ResourceUtils.getFile("classpath:valid-content.html");
        Document document = Jsoup.parse(file, "UTF-8");

        List<GameScore> gameScores = contentParser.parse(document);

        assertThat(gameScores).isNotNull();
        assertThat(gameScores.size()).isEqualTo(100);
        assertThat(gameScores.get(0).getTitle()).isEqualTo("Red Dead Redemption 2");
        assertThat(gameScores.get(0).getScore()).isEqualTo(97);
        assertThat(gameScores.get(1).getTitle()).isEqualTo("Grand Theft Auto V");
        assertThat(gameScores.get(1).getScore()).isEqualTo(97);
        assertThat(gameScores.get(2).getTitle()).isEqualTo("Persona 5 Royal");
        assertThat(gameScores.get(2).getScore()).isEqualTo(95);
    }

    @Test
    void testDocumentWithEmptyContent() throws IOException {
        File file = ResourceUtils.getFile("classpath:empty-content.html");
        Document document = Jsoup.parse(file, "UTF-8");

        List<GameScore> gameScores = contentParser.parse(document);

        assertThat(gameScores.size()).isEqualTo(0);
    }
}