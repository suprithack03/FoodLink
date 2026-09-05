package com.foodlink.foodlink.controller;

import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.service.FoodPostService;
import org.springframework.http.ResponseEntity;
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
    public FoodPost createFoodPost(@RequestBody FoodPost foodPost) {
        return foodPostService.createFoodPost(foodPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFoodPost(
            @PathVariable Long id,
            @RequestBody FoodPost updatedFoodPost) {

        try {
            FoodPost updated =
                    foodPostService.updateFoodPost(id, updatedFoodPost);

            if (updated == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(updated);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteFoodPost(@PathVariable Long id) {
        foodPostService.deleteFoodPost(id);
    }
}