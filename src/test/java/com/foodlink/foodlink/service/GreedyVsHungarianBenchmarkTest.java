package com.foodlink.foodlink.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GreedyVsHungarianBenchmarkTest {

    @Test
    void shouldCompareHungarianAndGreedyAcrossMultipleCases() {

        double[][][] testMatrices = {

                {
                        {1, 2},
                        {2, 100}
                },

                {
                        {4, 8, 6},
                        {7, 3, 9},
                        {5, 10, 2}
                },

                {
                        {10, 2, 9, 7},
                        {6, 8, 3, 5},
                        {4, 7, 6, 2},
                        {9, 5, 8, 4}
                },

                {
                        {3, 9, 2, 8, 7},
                        {6, 4, 8, 3, 9},
                        {7, 5, 6, 4, 2},
                        {8, 3, 7, 6, 5},
                        {2, 8, 4, 9, 3}
                }
        };

        HungarianAlgorithm hungarianAlgorithm =
                new HungarianAlgorithm();

        GreedyMatchingService greedyMatchingService =
                new GreedyMatchingService();

        double totalHungarianCost = 0;
        double totalGreedyCost = 0;

        int casesWhereHungarianIsBetter = 0;

        for (int i = 0; i < testMatrices.length; i++) {

            double[][] costMatrix = testMatrices[i];

            int[] hungarianAssignment =
                    hungarianAlgorithm.solve(costMatrix);

            int[] greedyAssignment =
                    greedyMatchingService.solve(costMatrix);

            double hungarianCost =
                    calculateTotalCost(
                            costMatrix,
                            hungarianAssignment
                    );

            double greedyCost =
                    calculateTotalCost(
                            costMatrix,
                            greedyAssignment
                    );

            totalHungarianCost += hungarianCost;
            totalGreedyCost += greedyCost;

            if (hungarianCost < greedyCost) {
                casesWhereHungarianIsBetter++;
            }

            System.out.println(
                    "Case " + (i + 1)
                            + " - Hungarian: "
                            + hungarianCost
                            + ", Greedy: "
                            + greedyCost
            );
        }

        double percentageImprovement =
                ((totalGreedyCost - totalHungarianCost)
                        / totalGreedyCost) * 100.0;

        System.out.println();
        System.out.println(
                "Total Hungarian cost: "
                        + totalHungarianCost
        );

        System.out.println(
                "Total Greedy cost: "
                        + totalGreedyCost
        );

        System.out.println(
                "Hungarian better in "
                        + casesWhereHungarianIsBetter
                        + " out of "
                        + testMatrices.length
                        + " cases"
        );

        System.out.println(
                "Overall cost improvement: "
                        + percentageImprovement
                        + "%"
        );

        assertTrue(
                totalHungarianCost <= totalGreedyCost
        );

        assertTrue(
                casesWhereHungarianIsBetter > 0
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
