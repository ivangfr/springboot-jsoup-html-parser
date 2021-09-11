package com.mycompany.gamescoreapi.rest.dto;

import lombok.Value;

@Value
public class GameScoreResponse {

    long id;
    String title;
    int score;
}
