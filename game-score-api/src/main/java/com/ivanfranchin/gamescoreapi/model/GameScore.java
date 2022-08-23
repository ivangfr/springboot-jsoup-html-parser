package com.ivanfranchin.gamescoreapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "gamescores")
public class GameScore {

    @Id
    private Long id;

    @TextIndexed
    private String title;

    private int score;
    private LocalDateTime lastModifiedDate;

    public GameScore(Long id, String title, int score) {
        this.id = id;
        this.title = title;
        this.score = score;
    }
}
