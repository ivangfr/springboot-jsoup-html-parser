package com.mycompany.gamescorecollector.client.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Validated
@ConfigurationProperties(prefix = "website")
public class WebsiteProperties {

    @NotBlank
    private String url;

    @Min(0)
    private int minPage;

    @Min(0)
    private int maxPage;

    @Min(0)
    private int numThreads;

}
