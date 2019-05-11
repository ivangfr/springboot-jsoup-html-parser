package com.mycompany.gamescoreapi;


import com.mycompany.gamescoreapi.model.GameScore;
import com.mycompany.gamescoreapi.repository.GameScoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GameScoreApiApplicationTests {

    @Autowired
    private GameScoreRepository gameScoreRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void givenNoGameScore_testGetAllGameScore() {
        ResponseEntity<GameScore[]> responseEntity = testRestTemplate.getForEntity("/api/games", GameScore[].class);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertNotNull(responseEntity.getBody());
        assertEquals(responseEntity.getBody().length, 0);
    }

    @Test
    void givenOneGameScore_testGetAllGameScore() {
        gameScoreRepository.save(new GameScore("Resident Evil 2", 91));

        ResponseEntity<GameScore[]> responseEntity = testRestTemplate.getForEntity("/api/games", GameScore[].class);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertNotNull(responseEntity.getBody());
        assertEquals(responseEntity.getBody().length, 1);
        assertEquals(responseEntity.getBody()[0].getTitle(), "Resident Evil 2");
        assertEquals(responseEntity.getBody()[0].getScore(), 91);
    }

    @Test
    void givenNonExistentGameTitle_testGetGameScoreByTitle() {
        ResponseEntity<GameScore> responseEntity = testRestTemplate.getForEntity("/api/games/Soccer", GameScore.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void givenExistentGameTitle_testGetGameScoreByTitle() {
        gameScoreRepository.save(new GameScore("FIFA 2019", 95));

        ResponseEntity<GameScore> responseEntity = testRestTemplate.getForEntity("/api/games/FIFA 2019", GameScore.class);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertNotNull(responseEntity.getBody());
        assertEquals(responseEntity.getBody().getTitle(), "FIFA 2019");
        assertEquals(responseEntity.getBody().getScore(), 95);
    }

    // TODO
    @Test
    void givenNoGameScore_testGetAllGameScoreByPage() {
        ParameterizedTypeReference<RestResponsePageImpl<GameScore>> responseType = new ParameterizedTypeReference<RestResponsePageImpl<GameScore>>() {};
        ResponseEntity<RestResponsePageImpl<GameScore>> responseEntity = testRestTemplate.exchange("/api/pg_games", HttpMethod.GET,null, responseType);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertNotNull(responseEntity.getBody());
        assertEquals(responseEntity.getBody().getTotalPages(), 0);
        assertEquals(responseEntity.getBody().getTotalElements(), 0);
        assertEquals(responseEntity.getBody().getContent().size(), 0);
    }

    // TODO
    @Test
    void givenOneGameScore_testGetAllGameScoreByPage() {
        gameScoreRepository.save(new GameScore("FIFA 2019", 95));
        gameScoreRepository.save(new GameScore("Resident Evil 2", 91));

        ParameterizedTypeReference<RestResponsePageImpl<GameScore>> responseType = new ParameterizedTypeReference<RestResponsePageImpl<GameScore>>() {};
        ResponseEntity<RestResponsePageImpl<GameScore>> responseEntity = testRestTemplate.exchange("/api/pg_games", HttpMethod.GET,null, responseType);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertNotNull(responseEntity.getBody());
        assertEquals(responseEntity.getBody().getTotalPages(), 1);
        assertEquals(responseEntity.getBody().getTotalElements(), 2);
        assertEquals(responseEntity.getBody().getNumberOfElements(), 2);
        assertEquals(responseEntity.getBody().getContent().size(), 2);
    }

}
