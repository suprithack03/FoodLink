package com.foodlink.foodlink;

import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.service.DistanceService;
import com.foodlink.foodlink.service.MatchingCostService;
import com.foodlink.foodlink.service.UrgencyService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatchingCostServiceTest {

    @Test
    void shouldCalculateCombinedMatchingCost() {

        FoodPost foodPost = new FoodPost();

        foodPost.setLatitude(12.9716);
        foodPost.setLongitude(77.5946);

        foodPost.setPostedAt(
                LocalDateTime.of(2026, 9, 5, 10, 0)
        );

        foodPost.setShelfLifeHours(8.0);

        DistanceService distanceService =
                new DistanceService();

        UrgencyService urgencyService =
                new UrgencyService();

        MatchingCostService matchingCostService =
                new MatchingCostService(
                        distanceService,
                        urgencyService
                );

        LocalDateTime currentTime =
                LocalDateTime.of(2026, 9, 5, 14, 0);

        double cost =
                matchingCostService.calculateCost(
                        foodPost,
                        12.9716,
                        77.5946,
                        currentTime
                );

        // Same location → distance = 0
        // 50% shelf life remaining → urgency = 0.875
        // cost = 0 + 40 × (1 - 0.875) = 5
        assertEquals(5.0, cost, 0.0001);
    }
}