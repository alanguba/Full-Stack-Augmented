package com.api.agb.itera.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PexelsRestClientConfig {

    @Bean
    public RestClient pexelsRestClient(
            @Value("${pexels.api-key}") String apiKey
    ) {
        return RestClient.builder()
                .baseUrl("https://api.pexels.com/v1")
                .defaultHeader("Authorization", apiKey)
                .build();
    }

}
