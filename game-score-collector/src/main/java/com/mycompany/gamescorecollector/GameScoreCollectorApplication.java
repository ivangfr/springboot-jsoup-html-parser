package com.mycompany.gamescorecollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication
public class GameScoreCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameScoreCollectorApplication.class, args);
    }

}
