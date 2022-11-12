package com.ivanfranchin.gamescoreapi;


import com.ivanfranchin.gamescoreapi.model.GameScore;
import com.ivanfranchin.gamescoreapi.repository.GameScoreRepository;
import com.ivanfranchin.gamescoreapi.rest.dto.GameScoreResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameScoreApiApplicationTests {

    @Autowired
    private GameScoreRepository gameScoreRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    void setUp() {
        gameScoreRepository.deleteAll();
    }

    @Test
    void givenNonExistingGameIdTestGetGameScore() {
        ResponseEntity<GameScoreResponse> responseEntity = testRestTemplate.getForEntity("/api/games/1", GameScoreResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void givenExistingGameIdTestGetGameScore() {
        gameScoreRepository.save(new GameScore(1L, "FIFA 2019", 95));

        ResponseEntity<GameScoreResponse> responseEntity = testRestTemplate.getForEntity("/api/games/1", GameScoreResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(1);
        assertThat(responseEntity.getBody().title()).isEqualTo("FIFA 2019");
        assertThat(responseEntity.getBody().score()).isEqualTo(95);
    }

    @Test
    void givenNoGameScoreTestGetGameScores() {
        ParameterizedTypeReference<RestResponsePageImpl<GameScoreResponse>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<RestResponsePageImpl<GameScoreResponse>> responseEntity = testRestTemplate.exchange("/api/games", HttpMethod.GET, null, responseType);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getTotalPages()).isEqualTo(0);
        assertThat(responseEntity.getBody().getTotalElements()).isEqualTo(0);
        assertThat(responseEntity.getBody().getContent().size()).isEqualTo(0);
    }

    @Test
    void givenSomeGameScoresTestGetGameScores() {
        gameScoreRepository.save(new GameScore(1L, "FIFA 2019", 95));
        gameScoreRepository.save(new GameScore(2L, "Resident Evil 2", 91));

        ParameterizedTypeReference<RestResponsePageImpl<GameScoreResponse>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<RestResponsePageImpl<GameScoreResponse>> responseEntity = testRestTemplate.exchange("/api/games", HttpMethod.GET, null, responseType);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getTotalPages()).isEqualTo(1);
        assertThat(responseEntity.getBody().getTotalElements()).isEqualTo(2);
        assertThat(responseEntity.getBody().getNumberOfElements()).isEqualTo(2);
        assertThat(responseEntity.getBody().getContent().size()).isEqualTo(2);
    }

    @Test
    void givenSomeGameScoresTestGetGameScoresFilteredByTitle() {
        gameScoreRepository.save(new GameScore(1L, "FIFA 2019", 95));
        gameScoreRepository.save(new GameScore(2L, "Resident Evil 2", 91));

        ParameterizedTypeReference<RestResponsePageImpl<GameScoreResponse>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<RestResponsePageImpl<GameScoreResponse>> responseEntity = testRestTemplate.exchange("/api/games?title=fifa", HttpMethod.GET, null, responseType);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getTotalPages()).isEqualTo(1);
        assertThat(responseEntity.getBody().getTotalElements()).isEqualTo(1);
        assertThat(responseEntity.getBody().getNumberOfElements()).isEqualTo(1);
        assertThat(responseEntity.getBody().getContent().size()).isEqualTo(1);
    }
}
