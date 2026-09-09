package com.foodlink.foodlink;

import com.foodlink.foodlink.service.DistanceService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DistanceServiceTest {

    @Test
    void shouldCalculateDistanceBetweenTwoLocations() {

        DistanceService distanceService = new DistanceService();

        double distance = distanceService.calculateDistance(
                12.9716,
                77.5946,
                12.9352,
                77.6245
        );

        System.out.println(
                "Calculated distance: " + distance + " km"
        );

        assertTrue(distance > 0);
    }
}