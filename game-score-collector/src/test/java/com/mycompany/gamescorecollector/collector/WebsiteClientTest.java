package com.mycompany.gamescorecollector.collector;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import(WebsiteClient.class)
@AutoConfigureWireMock(port = 9090)
class WebsiteClientTest {

    @Autowired
    private WebsiteClient websiteClient;

    @Test
    void testCall() {
        stubFor(get(urlEqualTo("/test")).willReturn(aResponse()
                .withHeader("Content-Type", "text/html")
                .withBody("<html><head></head><body>Hello World!</body></html>")));

        Document document = websiteClient.call("http://localhost:9090/test");

        assertThat(document).isNotNull();
        assertThat(document.select("body").html()).isEqualTo("Hello World!");
    }
}