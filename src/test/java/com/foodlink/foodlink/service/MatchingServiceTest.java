package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.entity.Ngo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchingServiceTest {

    @Test
    void shouldProduceOptimalMatches() {

        DistanceService distanceService =
                new DistanceService();

        UrgencyService urgencyService =
                new UrgencyService();

        MatchingCostService matchingCostService =
                new MatchingCostService(
                        distanceService,
                        urgencyService
                );

        CostMatrixService costMatrixService =
                new CostMatrixService(
                        matchingCostService
                );

        NgoSlotService ngoSlotService =
                new NgoSlotService();

        HungarianAlgorithm hungarianAlgorithm =
                new HungarianAlgorithm();

        MatchingService matchingService =
                new MatchingService(
                        ngoSlotService,
                        costMatrixService,
                        hungarianAlgorithm,
                        null,
                        null
                );

        LocalDateTime currentTime =
                LocalDateTime.of(
                        2026,
                        9,
                        7,
                        10,
                        0
                );

        FoodPost foodPost1 =
                new FoodPost();

        foodPost1.setId(1L);
        foodPost1.setLatitude(12.9716);
        foodPost1.setLongitude(77.5946);
        foodPost1.setPostedAt(
                LocalDateTime.of(
                        2026,
                        9,
                        7,
                        8,
                        0
                )
        );
        foodPost1.setShelfLifeHours(8.0);

        FoodPost foodPost2 =
                new FoodPost();

        foodPost2.setId(2L);
        foodPost2.setLatitude(12.9352);
        foodPost2.setLongitude(77.6245);
        foodPost2.setPostedAt(
                LocalDateTime.of(
                        2026,
                        9,
                        7,
                        8,
                        0
                )
        );
        foodPost2.setShelfLifeHours(8.0);

        Ngo ngo1 =
                new Ngo();

        ngo1.setId(10L);
        ngo1.setLatitude(12.9716);
        ngo1.setLongitude(77.5946);
        ngo1.setCapacity(1);

        Ngo ngo2 =
                new Ngo();

        ngo2.setId(20L);
        ngo2.setLatitude(12.9352);
        ngo2.setLongitude(77.6245);
        ngo2.setCapacity(1);

        List<FoodPost> foodPosts =
                List.of(
                        foodPost1,
                        foodPost2
                );

        List<Ngo> ngos =
                List.of(
                        ngo1,
                        ngo2
                );

        List<MatchResult> matches =
                matchingService.findMatches(
                        foodPosts,
                        ngos,
                        currentTime
                );

        assertEquals(2, matches.size());

        assertEquals(
                1L,
                matches.get(0).getFoodPostId()
        );

        assertEquals(
                10L,
                matches.get(0).getNgoId()
        );

        assertEquals(
                2L,
                matches.get(1).getFoodPostId()
        );

        assertEquals(
                20L,
                matches.get(1).getNgoId()
        );
    }

    @Test
    void shouldRespectNgoCapacity() {

        DistanceService distanceService =
                new DistanceService();

        UrgencyService urgencyService =
                new UrgencyService();

        MatchingCostService matchingCostService =
                new MatchingCostService(
                        distanceService,
                        urgencyService
                );

        CostMatrixService costMatrixService =
                new CostMatrixService(
                        matchingCostService
                );

        NgoSlotService ngoSlotService =
                new NgoSlotService();

        HungarianAlgorithm hungarianAlgorithm =
                new HungarianAlgorithm();

        MatchingService matchingService =
                new MatchingService(
                        ngoSlotService,
                        costMatrixService,
                        hungarianAlgorithm,
                        null,
                        null
                );

        LocalDateTime currentTime =
                LocalDateTime.of(
                        2026,
                        9,
                        7,
                        10,
                        0
                );

        FoodPost foodPost1 =
                new FoodPost();

        foodPost1.setId(1L);
        foodPost1.setLatitude(12.9716);
        foodPost1.setLongitude(77.5946);
        foodPost1.setPostedAt(
                LocalDateTime.of(
                        2026,
                        9,
                        7,
                        8,
                        0
                )
        );
        foodPost1.setShelfLifeHours(8.0);

        FoodPost foodPost2 =
                new FoodPost();

        foodPost2.setId(2L);
        foodPost2.setLatitude(12.9352);
        foodPost2.setLongitude(77.6245);
        foodPost2.setPostedAt(
                LocalDateTime.of(
                        2026,
                        9,
                        7,
                        8,
                        0
                )
        );
        foodPost2.setShelfLifeHours(8.0);

        Ngo ngo =
                new Ngo();

        ngo.setId(10L);
        ngo.setLatitude(12.9716);
        ngo.setLongitude(77.5946);
        ngo.setCapacity(2);

        List<FoodPost> foodPosts =
                List.of(
                        foodPost1,
                        foodPost2
                );

        List<Ngo> ngos =
                List.of(ngo);

        List<MatchResult> matches =
                matchingService.findMatches(
                        foodPosts,
                        ngos,
                        currentTime
                );

        assertEquals(2, matches.size());

        assertEquals(
                10L,
                matches.get(0).getNgoId()
        );

        assertEquals(
                10L,
                matches.get(1).getNgoId()
        );
    }
}

