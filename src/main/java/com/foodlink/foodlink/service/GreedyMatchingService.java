
package com.foodlink.foodlink.service;

import org.springframework.stereotype.Service;

@Service
public class GreedyMatchingService {

    public int[] solve(double[][] costMatrix) {

        validateMatrix(costMatrix);

        int n = costMatrix.length;

        int[] assignment = new int[n];

        boolean[] usedColumns = new boolean[n];

        for (int i = 0; i < n; i++) {

            double minimumCost =
                    Double.POSITIVE_INFINITY;

            int bestColumn = -1;

            for (int j = 0; j < n; j++) {

                if (!usedColumns[j]
                        && costMatrix[i][j] < minimumCost) {

                    minimumCost =
                            costMatrix[i][j];

                    bestColumn = j;
                }
            }

            assignment[i] = bestColumn;

            usedColumns[bestColumn] = true;
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


