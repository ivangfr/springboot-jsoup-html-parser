package com.mycompany.gamescorecollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.data.mongodb.config.EnableMongoAuditing;

//@EnableMongoAuditing // disabled for now, otherwise it's not possible to build the Docker native image
@SpringBootApplication
public class GameScoreCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameScoreCollectorApplication.class, args);
    }

}
