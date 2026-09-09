package com.foodlink.foodlink;

import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.service.UrgencyService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrgencyServiceTest {

    @Test
    void shouldCalculateUrgencyFromFoodPost() {

        UrgencyService urgencyService =
                new UrgencyService();

        FoodPost foodPost = new FoodPost();

        foodPost.setPostedAt(
                LocalDateTime.of(2026, 9, 5, 10, 0)
        );

        foodPost.setShelfLifeHours(8.0);

        LocalDateTime currentTime =
                LocalDateTime.of(2026, 9, 5, 14, 0);

        double urgency =
                urgencyService.calculateUrgency(
                        foodPost,
                        currentTime
                );

        assertEquals(
                0.875,
                urgency,
                0.0001
        );
    }
}