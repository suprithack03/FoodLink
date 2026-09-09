package com.foodlink.foodlink;

import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.service.CostMatrixService;
import com.foodlink.foodlink.service.DistanceService;
import com.foodlink.foodlink.service.MatchingCostService;
import com.foodlink.foodlink.service.NgoSlot;
import com.foodlink.foodlink.service.UrgencyService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CostMatrixServiceTest {

    @Test
    void shouldBuildTwoByTwoCostMatrix() {

        LocalDateTime postedAt =
                LocalDateTime.of(
                        2026,
                        9,
                        5,
                        10,
                        0
                );

        LocalDateTime currentTime =
                LocalDateTime.of(
                        2026,
                        9,
                        5,
                        14,
                        0
                );

        FoodPost foodPost1 =
                new FoodPost();

        foodPost1.setLatitude(12.9716);
        foodPost1.setLongitude(77.5946);
        foodPost1.setPostedAt(postedAt);
        foodPost1.setShelfLifeHours(8.0);


        FoodPost foodPost2 =
                new FoodPost();

        foodPost2.setLatitude(12.9716);
        foodPost2.setLongitude(77.5946);
        foodPost2.setPostedAt(postedAt);
        foodPost2.setShelfLifeHours(8.0);


        NgoSlot ngoSlot1 =
                new NgoSlot(
                        1L,
                        12.9716,
                        77.5946
                );

        NgoSlot ngoSlot2 =
                new NgoSlot(
                        2L,
                        12.9716,
                        77.5946
                );


        DistanceService distanceService =
                new DistanceService();

        UrgencyService urgencyService =
                new UrgencyService();

        MatchingCostService matchingCostService =
                new MatchingCostService(
                        distanceService,
                        urgencyService
                );

        CostMatrixService service =
                new CostMatrixService(
                        matchingCostService
                );


        double[][] matrix =
                service.buildCostMatrix(
                        List.of(
                                foodPost1,
                                foodPost2
                        ),
                        List.of(
                                ngoSlot1,
                                ngoSlot2
                        ),
                        currentTime
                );


        assertEquals(2, matrix.length);
        assertEquals(2, matrix[0].length);

        assertEquals(5.0, matrix[0][0]);
        assertEquals(5.0, matrix[0][1]);
        assertEquals(5.0, matrix[1][0]);
        assertEquals(5.0, matrix[1][1]);
    }
}