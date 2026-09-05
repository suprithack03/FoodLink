package com.foodlink.foodlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.foodlink.foodlink")
public class FoodlinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodlinkApplication.class, args);
    }

}