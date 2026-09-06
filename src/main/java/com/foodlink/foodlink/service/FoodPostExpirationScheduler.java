package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.entity.FoodPostStatus;
import com.foodlink.foodlink.repository.FoodPostRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FoodPostExpirationScheduler {

    private final FoodPostRepository foodPostRepository;
    private final RequestService requestService;

    public FoodPostExpirationScheduler(
            FoodPostRepository foodPostRepository,
            RequestService requestService) {

        this.foodPostRepository = foodPostRepository;
        this.requestService = requestService;
    }

    @Scheduled(fixedRate = 60000)
    public void expireFoodPosts() {

        List<FoodPost> expiredPosts =
                foodPostRepository
                        .findByStatusAndAvailableUntilLessThanEqual(
                                FoodPostStatus.PENDING,
                                LocalDateTime.now()
                        );

        for (FoodPost foodPost : expiredPosts) {

            requestService.expireFoodPost(foodPost.getId());
        }
    }
}
