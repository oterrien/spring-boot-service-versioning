package com.ote.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class RoutingConfigurationServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RoutingConfigurationServiceApplication.class).run(args);
    }
}