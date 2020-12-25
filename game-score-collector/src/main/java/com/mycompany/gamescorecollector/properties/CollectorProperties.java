package com.mycompany.gamescorecollector.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
@ConfigurationProperties(prefix = "collector")
public class CollectorProperties {

    @NotNull
    private Mode mode;

    @NotNull
    private Website website;

    public enum Mode {
        GET_AND_SAVE, GET_ALL_AND_SAVE_ALL
    }

    @Data
    @Valid
    public static class Website {

        @NotBlank
        private String url;

        @Min(0)
        private int minPage;

        @Min(0)
        private int maxPage;
    }

}
