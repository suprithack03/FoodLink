package com.foodlink.foodlink.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GreedyVsHungarianTest {

    @Test
    void shouldShowHungarianIsBetterThanGreedy() {

        double[][] costMatrix = {
                {1, 2},
                {2, 100}
        };

        HungarianAlgorithm hungarianAlgorithm =
                new HungarianAlgorithm();

        GreedyMatchingService greedyMatchingService =
                new GreedyMatchingService();

        int[] hungarianAssignment =
                hungarianAlgorithm.solve(
                        costMatrix
                );

        int[] greedyAssignment =
                greedyMatchingService.solve(
                        costMatrix
                );

        double hungarianTotalCost =
                calculateTotalCost(
                        costMatrix,
                        hungarianAssignment
                );

        double greedyTotalCost =
                calculateTotalCost(
                        costMatrix,
                        greedyAssignment
                );

        System.out.println(
                "Hungarian total cost: "
                        + hungarianTotalCost
        );

        System.out.println(
                "Greedy total cost: "
                        + greedyTotalCost
        );

        assertTrue(
                hungarianTotalCost < greedyTotalCost
        );
    }

    private double calculateTotalCost(
            double[][] costMatrix,
            int[] assignment) {

        double totalCost = 0;

        for (int i = 0; i < assignment.length; i++) {

            totalCost +=
                    costMatrix[i][assignment[i]];
        }

        return totalCost;
    }
}


