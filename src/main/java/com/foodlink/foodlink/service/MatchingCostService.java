package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.FoodPost;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MatchingCostService {

    private static final double DISTANCE_WEIGHT = 1.0;
    private static final double URGENCY_WEIGHT = 40.0;

    private final DistanceService distanceService;
    private final UrgencyService urgencyService;

    public MatchingCostService(
            DistanceService distanceService,
            UrgencyService urgencyService) {

        this.distanceService = distanceService;
        this.urgencyService = urgencyService;
    }

    public double calculateCost(
            FoodPost foodPost,
            double ngoLatitude,
            double ngoLongitude,
            LocalDateTime currentTime) {

        double distanceKm =
                distanceService.calculateDistance(
                        foodPost.getLatitude(),
                        foodPost.getLongitude(),
                        ngoLatitude,
                        ngoLongitude
                );

        double urgency =
                urgencyService.calculateUrgency(
                        foodPost,
                        currentTime
                );

        double cost =
                DISTANCE_WEIGHT * distanceKm
                        + URGENCY_WEIGHT * (1 - urgency);

        return cost;
    }
}