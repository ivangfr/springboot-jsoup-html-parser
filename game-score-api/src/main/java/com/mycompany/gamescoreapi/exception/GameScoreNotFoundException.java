package com.mycompany.gamescoreapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GameScoreNotFoundException extends RuntimeException {

    public GameScoreNotFoundException(String message) {
        super(message);
    }

}
