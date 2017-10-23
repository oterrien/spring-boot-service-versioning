package com.ote.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class TestServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(TestServiceApplication.class).run(args);
    }
}