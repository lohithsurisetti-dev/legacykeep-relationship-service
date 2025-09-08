package com.legacykeep.relationship.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for User Service integration.
 * 
 * Sets up RestTemplate for HTTP communication with User Service.
 */
@Configuration
public class UserServiceConfig {

    @Value("${user.service.timeout:5000}")
    private int timeoutMs;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        
        return new RestTemplate(factory);
    }
}

