package com.mycompany.gamescoreapi.rest.dto;

import lombok.Data;

@Data
public class GameScoreDto {

    private long id;
    private String title;
    private int score;

}
