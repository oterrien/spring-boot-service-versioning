package com.ote.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(UserServiceApplication.class).run(args);
    }
}