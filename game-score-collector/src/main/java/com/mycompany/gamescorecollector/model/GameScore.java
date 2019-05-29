package com.mycompany.gamescorecollector.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "gamescore")
public class GameScore {

    @Id
    private String title;

    private int score;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public GameScore(String title, int score) {
        this.title = title;
        this.score = score;
    }
}
