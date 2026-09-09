package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.FoodPost;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UrgencyService {

    public double calculateUrgency(
            FoodPost foodPost,
            LocalDateTime currentTime) {

        if (foodPost == null) {
            throw new IllegalArgumentException(
                    "Food post cannot be null"
            );
        }

        return calculateUrgency(
                foodPost.getPostedAt(),
                foodPost.getShelfLifeHours(),
                currentTime
        );
    }

    public double calculateUrgency(
            LocalDateTime postedAt,
            double shelfLifeHours,
            LocalDateTime currentTime) {

        if (postedAt == null) {
            throw new IllegalArgumentException(
                    "Posted time cannot be null"
            );
        }

        if (currentTime == null) {
            throw new IllegalArgumentException(
                    "Current time cannot be null"
            );
        }

        if (shelfLifeHours <= 0) {
            throw new IllegalArgumentException(
                    "Shelf life must be greater than 0"
            );
        }

        double hoursSincePosted =
                Duration.between(
                        postedAt,
                        currentTime
                ).toMinutes() / 60.0;

        double remainingFraction =
                1 - (hoursSincePosted / shelfLifeHours);

        // Keep remaining fraction between 0 and 1
        remainingFraction =
                Math.max(
                        0,
                        Math.min(1, remainingFraction)
                );

        double urgency =
                1 - Math.pow(remainingFraction, 3);

        return urgency;
    }
}