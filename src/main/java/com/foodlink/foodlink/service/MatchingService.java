
package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.entity.FoodPostStatus;
import com.foodlink.foodlink.entity.Ngo;
import com.foodlink.foodlink.repository.FoodPostRepository;
import com.foodlink.foodlink.repository.NgoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchingService {

    private final NgoSlotService ngoSlotService;
    private final CostMatrixService costMatrixService;
    private final HungarianAlgorithm hungarianAlgorithm;
    private final FoodPostRepository foodPostRepository;
    private final NgoRepository ngoRepository;

    public MatchingService(
            NgoSlotService ngoSlotService,
            CostMatrixService costMatrixService,
            HungarianAlgorithm hungarianAlgorithm,
            FoodPostRepository foodPostRepository,
            NgoRepository ngoRepository) {

        this.ngoSlotService = ngoSlotService;
        this.costMatrixService = costMatrixService;
        this.hungarianAlgorithm = hungarianAlgorithm;
        this.foodPostRepository = foodPostRepository;
        this.ngoRepository = ngoRepository;
    }

    public List<MatchResult> findMatches(
            List<FoodPost> foodPosts,
            List<Ngo> ngos,
            LocalDateTime currentTime) {

        if (foodPosts == null || foodPosts.isEmpty()) {
            return List.of();
        }

        if (ngos == null || ngos.isEmpty()) {
            return List.of();
        }

        List<FoodPost> validFoodPosts =
                foodPosts.stream()
                        .filter(foodPost ->
                                foodPost.getId() != null
                                        && foodPost.getLatitude() != null
                                        && foodPost.getLongitude() != null
                                        && foodPost.getPostedAt() != null
                                        && foodPost.getShelfLifeHours() != null
                                        && foodPost.getShelfLifeHours() > 0
                        )
                        .toList();

        if (validFoodPosts.isEmpty()) {
            return List.of();
        }

        List<NgoSlot> ngoSlots =
                ngoSlotService.expandCapacity(ngos);

        if (ngoSlots.isEmpty()) {
            return List.of();
        }

        double[][] costMatrix =
                costMatrixService.buildCostMatrix(
                        validFoodPosts,
                        ngoSlots,
                        currentTime
                );

        int[] assignment =
                hungarianAlgorithm.solve(costMatrix);

        List<MatchResult> matches =
                new ArrayList<>();

        for (int foodPostIndex = 0;
             foodPostIndex < validFoodPosts.size();
             foodPostIndex++) {

            int ngoSlotIndex =
                    assignment[foodPostIndex];

            if (ngoSlotIndex >= ngoSlots.size()) {
                continue;
            }

            FoodPost foodPost =
                    validFoodPosts.get(foodPostIndex);

            NgoSlot ngoSlot =
                    ngoSlots.get(ngoSlotIndex);

            double cost =
                    costMatrix[
                            foodPostIndex
                    ][
                            ngoSlotIndex
                    ];

            matches.add(
                    new MatchResult(
                            foodPost.getId(),
                            ngoSlot.getNgoId(),
                            cost
                    )
            );
        }

        return matches;
    }

    public List<MatchResult> findMatchesFromDatabase(
            LocalDateTime currentTime) {

        List<FoodPost> foodPosts =
                foodPostRepository
                        .findByStatusAndAvailableUntilGreaterThan(
                                FoodPostStatus.PENDING,
                                currentTime
                        );

        List<Ngo> ngos =
                ngoRepository.findAll();

        return findMatches(
                foodPosts,
                ngos,
                currentTime
        );
    }
}

