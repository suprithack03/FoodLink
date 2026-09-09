package com.foodlink.foodlink.service;

import org.springframework.stereotype.Service;

@Service
public class HungarianAlgorithm {

    public int[] solve(double[][] costMatrix) {

        validateMatrix(costMatrix);

        int n = costMatrix.length;

        double[] u = new double[n + 1];
        double[] v = new double[n + 1];

        int[] p = new int[n + 1];
        int[] way = new int[n + 1];

        for (int i = 1; i <= n; i++) {

            p[0] = i;

            int j0 = 0;

            double[] minv = new double[n + 1];
            boolean[] used = new boolean[n + 1];

            for (int j = 0; j <= n; j++) {
                minv[j] = Double.POSITIVE_INFINITY;
            }

            do {
                used[j0] = true;

                int i0 = p[j0];

                double delta =
                        Double.POSITIVE_INFINITY;

                int j1 = 0;

                for (int j = 1; j <= n; j++) {

                    if (!used[j]) {

                        double current =
                                costMatrix[i0 - 1][j - 1]
                                        - u[i0]
                                        - v[j];

                        if (current < minv[j]) {
                            minv[j] = current;
                            way[j] = j0;
                        }

                        if (minv[j] < delta) {
                            delta = minv[j];
                            j1 = j;
                        }
                    }
                }

                for (int j = 0; j <= n; j++) {

                    if (used[j]) {
                        u[p[j]] += delta;
                        v[j] -= delta;
                    } else {
                        minv[j] -= delta;
                    }
                }

                j0 = j1;

            } while (p[j0] != 0);

            do {

                int j1 = way[j0];

                p[j0] = p[j1];

                j0 = j1;

            } while (j0 != 0);
        }

        int[] assignment = new int[n];

        for (int j = 1; j <= n; j++) {

            if (p[j] != 0) {
                assignment[p[j] - 1] = j - 1;
            }
        }

        return assignment;
    }

    private void validateMatrix(double[][] costMatrix) {

        if (costMatrix == null
                || costMatrix.length == 0) {

            throw new IllegalArgumentException(
                    "Cost matrix cannot be empty"
            );
        }

        int n = costMatrix.length;

        for (double[] row : costMatrix) {

            if (row == null || row.length != n) {

                throw new IllegalArgumentException(
                        "Cost matrix must be square"
                );
            }
        }
    }
}