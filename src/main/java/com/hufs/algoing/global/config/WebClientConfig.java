package com.hufs.algoing.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    //  @Value("${openai.url}")
    private String aiUrl;

    //   @Value("${openai.secret}")
    private String aiKey;

    @Bean
    public WebClient webClient() {
        return WebClient
                .builder()
                .baseUrl(aiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiKey)
                .build();
    }


}