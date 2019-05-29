package com.mycompany.gamescoreapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication
public class GameScoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameScoreApiApplication.class, args);
    }

}
