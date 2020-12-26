package com.mycompany.gamescoreapi;


import com.mycompany.gamescoreapi.model.GameScore;
import com.mycompany.gamescoreapi.repository.GameScoreRepository;
import com.mycompany.gamescoreapi.rest.dto.GameScoreDto;
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
    void givenNonExistingGameIdTestGetGameScore() {
        ResponseEntity<GameScoreDto> responseEntity = testRestTemplate.getForEntity("/api/games/1", GameScoreDto.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void givenExistingGameIdTestGetGameScore() {
        gameScoreRepository.save(new GameScore(1L, "FIFA 2019", 95));

        ResponseEntity<GameScoreDto> responseEntity = testRestTemplate.getForEntity("/api/games/1", GameScoreDto.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().getId());
        assertEquals("FIFA 2019", responseEntity.getBody().getTitle());
        assertEquals(95, responseEntity.getBody().getScore());
    }

    @Test
    void givenNoGameScoreTestGetGameScores() {
        ParameterizedTypeReference<RestResponsePageImpl<GameScoreDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<RestResponsePageImpl<GameScoreDto>> responseEntity = testRestTemplate.exchange("/api/games", HttpMethod.GET, null, responseType);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(0, responseEntity.getBody().getTotalPages());
        assertEquals(0, responseEntity.getBody().getTotalElements());
        assertEquals(0, responseEntity.getBody().getContent().size());
    }

    @Test
    void givenSomeGameScoresTestGetGameScores() {
        gameScoreRepository.save(new GameScore(1L, "FIFA 2019", 95));
        gameScoreRepository.save(new GameScore(2L, "Resident Evil 2", 91));

        ParameterizedTypeReference<RestResponsePageImpl<GameScoreDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<RestResponsePageImpl<GameScoreDto>> responseEntity = testRestTemplate.exchange("/api/games", HttpMethod.GET, null, responseType);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().getTotalPages());
        assertEquals(2, responseEntity.getBody().getTotalElements());
        assertEquals(2, responseEntity.getBody().getNumberOfElements());
        assertEquals(2, responseEntity.getBody().getContent().size());
    }

    @Test
    void givenSomeGameScoresTestGetGameScoresFilteredByTitle() {
        gameScoreRepository.save(new GameScore(1L, "FIFA 2019", 95));
        gameScoreRepository.save(new GameScore(2L, "Resident Evil 2", 91));

        ParameterizedTypeReference<RestResponsePageImpl<GameScoreDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<RestResponsePageImpl<GameScoreDto>> responseEntity = testRestTemplate.exchange("/api/games?title=fifa", HttpMethod.GET, null, responseType);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().getTotalPages());
        assertEquals(1, responseEntity.getBody().getTotalElements());
        assertEquals(1, responseEntity.getBody().getNumberOfElements());
        assertEquals(1, responseEntity.getBody().getContent().size());
    }

}
