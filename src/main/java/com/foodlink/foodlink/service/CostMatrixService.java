package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.FoodPost;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CostMatrixService {

    private static final double DUMMY_COST = 1_000_000.0;

    private final MatchingCostService matchingCostService;

    public CostMatrixService(
            MatchingCostService matchingCostService) {

        this.matchingCostService = matchingCostService;
    }

    public double[][] buildCostMatrix(
            List<FoodPost> foodPosts,
            List<NgoSlot> ngoSlots,
            LocalDateTime currentTime) {

        int numberOfFoodPosts =
                foodPosts.size();

        int numberOfNgoSlots =
                ngoSlots.size();

        int size = Math.max(
                numberOfFoodPosts,
                numberOfNgoSlots
        );

        double[][] costMatrix =
                new double[size][size];

        for (int i = 0; i < size; i++) {

            for (int j = 0; j < size; j++) {

                if (i < numberOfFoodPosts
                        && j < numberOfNgoSlots) {

                    FoodPost foodPost =
                            foodPosts.get(i);

                    NgoSlot ngoSlot =
                            ngoSlots.get(j);

                    costMatrix[i][j] =
                            matchingCostService.calculateCost(
                                    foodPost,
                                    ngoSlot.getLatitude(),
                                    ngoSlot.getLongitude(),
                                    currentTime
                            );

                } else {

                    costMatrix[i][j] =
                            DUMMY_COST;
                }
            }
        }

        return costMatrix;
    }
}