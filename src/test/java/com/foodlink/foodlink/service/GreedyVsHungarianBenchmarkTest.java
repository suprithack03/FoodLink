package com.foodlink.foodlink.service;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GreedyVsHungarianBenchmarkTest {

    private static final int NUMBER_OF_CASES = 100;
    private static final int MATRIX_SIZE = 10;

    @Test
    void shouldCompareHungarianAndGreedyAcrossGeneratedCases() {

        HungarianAlgorithm hungarianAlgorithm =
                new HungarianAlgorithm();

        GreedyMatchingService greedyMatchingService =
                new GreedyMatchingService();

        Random random = new Random(42);

        double totalHungarianCost = 0;
        double totalGreedyCost = 0;

        int hungarianBetterCount = 0;
        int greedyBetterCount = 0;
        int equalCount = 0;

        for (int testCase = 1;
             testCase <= NUMBER_OF_CASES;
             testCase++) {

            double[][] costMatrix =
                    generateCostMatrix(random);

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

                hungarianBetterCount++;

            } else if (greedyCost < hungarianCost) {

                greedyBetterCount++;

            } else {

                equalCount++;
            }
        }

        double percentageImprovement =
                ((totalGreedyCost - totalHungarianCost)
                        / totalGreedyCost) * 100.0;

        System.out.println();
        System.out.println(
                "Benchmark cases: "
                        + NUMBER_OF_CASES
        );

        System.out.println(
                "Matrix size: "
                        + MATRIX_SIZE
                        + " x "
                        + MATRIX_SIZE
        );

        System.out.println(
                "Total Hungarian cost: "
                        + totalHungarianCost
        );

        System.out.println(
                "Total Greedy cost: "
                        + totalGreedyCost
        );

        System.out.println(
                "Hungarian better: "
                        + hungarianBetterCount
                        + " cases"
        );

        System.out.println(
                "Greedy better: "
                        + greedyBetterCount
                        + " cases"
        );

        System.out.println(
                "Equal: "
                        + equalCount
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
    }

    private double[][] generateCostMatrix(Random random) {

        double[][] costMatrix =
                new double[MATRIX_SIZE][MATRIX_SIZE];

        for (int i = 0; i < MATRIX_SIZE; i++) {

            for (int j = 0; j < MATRIX_SIZE; j++) {

                costMatrix[i][j] =
                        1 + random.nextInt(100);
            }
        }

        return costMatrix;
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