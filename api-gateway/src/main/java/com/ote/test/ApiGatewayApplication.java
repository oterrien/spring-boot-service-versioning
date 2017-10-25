package com.ote.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableZuulProxy
@SpringBootApplication
@EnableCircuitBreaker
public class ApiGatewayApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ApiGatewayApplication.class).run(args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}