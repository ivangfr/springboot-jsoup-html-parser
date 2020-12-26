package com.mycompany.gamescorecollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableMongoAuditing // disabled for now, otherwise it's not possible to build the docker native image
@SpringBootApplication
public class GameScoreCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameScoreCollectorApplication.class, args);
    }

}
