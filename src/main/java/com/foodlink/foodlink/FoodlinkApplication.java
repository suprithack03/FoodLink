package com.foodlink.foodlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FoodlinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodlinkApplication.class, args);
    }
}