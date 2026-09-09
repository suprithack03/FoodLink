package com.foodlink.foodlink.controller;

import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.entity.FoodPostStatus;
import com.foodlink.foodlink.entity.Ngo;
import com.foodlink.foodlink.repository.FoodPostRepository;
import com.foodlink.foodlink.repository.NgoRepository;
import com.foodlink.foodlink.service.MatchResult;
import com.foodlink.foodlink.service.MatchingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;
    private final FoodPostRepository foodPostRepository;
    private final NgoRepository ngoRepository;

    public MatchingController(
            MatchingService matchingService,
            FoodPostRepository foodPostRepository,
            NgoRepository ngoRepository) {

        this.matchingService = matchingService;
        this.foodPostRepository = foodPostRepository;
        this.ngoRepository = ngoRepository;
    }

    @GetMapping
    public List<MatchResponse> findMatches() {

        LocalDateTime currentTime = LocalDateTime.now();

        List<FoodPost> foodPosts =
                foodPostRepository.findByStatusAndAvailableUntilGreaterThan(
                        FoodPostStatus.PENDING,
                        currentTime
                );

        List<Ngo> ngos =
                ngoRepository.findAll();

        List<MatchResult> results =
                matchingService.findMatches(
                        foodPosts,
                        ngos,
                        currentTime
                );

        return results.stream()
                .map(result ->
                        new MatchResponse(
                                result.getFoodPostId(),
                                result.getNgoId(),
                                result.getCost()
                        )
                )
                .toList();
    }
}