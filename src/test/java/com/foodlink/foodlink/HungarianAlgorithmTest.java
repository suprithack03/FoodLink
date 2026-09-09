package com.foodlink.foodlink;

import com.foodlink.foodlink.service.HungarianAlgorithm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HungarianAlgorithmTest {

    @Test
    void shouldFindMinimumCostAssignment() {

        double[][] costMatrix = {
                {5.0, 10.0},
                {10.0, 5.0}
        };

        HungarianAlgorithm algorithm =
                new HungarianAlgorithm();

        int[] assignment =
                algorithm.solve(costMatrix);

        assertArrayEquals(
                new int[]{0, 1},
                assignment
        );
    }


    @Test
    void shouldFindGlobalMinimumInsteadOfGreedyRowMinimum() {

        double[][] costMatrix = {
                {1.0, 2.0},
                {2.0, 100.0}
        };

        HungarianAlgorithm algorithm =
                new HungarianAlgorithm();

        int[] assignment =
                algorithm.solve(costMatrix);

        assertArrayEquals(
                new int[]{1, 0},
                assignment
        );
    }


    @Test
    void shouldFindMinimumAssignmentForThreeByThreeMatrix() {

        double[][] costMatrix = {
                {4.0, 1.0, 3.0},
                {2.0, 0.0, 5.0},
                {3.0, 2.0, 2.0}
        };

        HungarianAlgorithm algorithm =
                new HungarianAlgorithm();

        int[] assignment =
                algorithm.solve(costMatrix);

        assertArrayEquals(
                new int[]{1, 0, 2},
                assignment
        );
    }


    @Test
    void shouldRejectNonSquareMatrix() {

        double[][] costMatrix = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
        };

        HungarianAlgorithm algorithm =
                new HungarianAlgorithm();

        assertThrows(
                IllegalArgumentException.class,
                () -> algorithm.solve(costMatrix)
        );
    }


    @Test
    void shouldRejectEmptyMatrix() {

        double[][] costMatrix = {};

        HungarianAlgorithm algorithm =
                new HungarianAlgorithm();

        assertThrows(
                IllegalArgumentException.class,
                () -> algorithm.solve(costMatrix)
        );
    }
}