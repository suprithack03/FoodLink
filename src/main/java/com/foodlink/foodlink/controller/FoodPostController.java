package com.foodlink.foodlink.controller;

import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.service.FoodPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food-posts")
public class FoodPostController {

    private final FoodPostService foodPostService;

    public FoodPostController(FoodPostService foodPostService) {
        this.foodPostService = foodPostService;
    }

    @GetMapping
    public List<FoodPost> getAllFoodPosts() {
        return foodPostService.getAllFoodPosts();
    }

    @GetMapping("/{id}")
    public FoodPost getFoodPostById(@PathVariable Long id) {
        return foodPostService.getFoodPostById(id);
    }

    @PostMapping
    public FoodPost createFoodPost(
            @RequestBody CreateFoodPostRequest request,
            Authentication authentication) {

        FoodPost foodPost = new FoodPost();

        foodPost.setFoodType(request.getFoodType());
        foodPost.setQuantity(request.getQuantity());
        foodPost.setDescription(request.getDescription());
        foodPost.setPhoto(request.getPhoto());
        foodPost.setPickupLocation(request.getPickupLocation());
        foodPost.setAvailableUntil(request.getAvailableUntil());

        String email = authentication.getName();

        return foodPostService.createFoodPost(foodPost, email);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFoodPost(
            @PathVariable Long id,
            @RequestBody FoodPost updatedFoodPost,
            Authentication authentication) {

        try {
            String email = authentication.getName();

            FoodPost updated =
                    foodPostService.updateFoodPost(
                            id,
                            updatedFoodPost,
                            email
                    );

            if (updated == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(updated);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelFoodPost(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            String email = authentication.getName();

            foodPostService.cancelFoodPost(id, email);

            return ResponseEntity.ok(
                    "Food post cancelled successfully"
            );

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}