package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.entity.FoodPostStatus;
import com.foodlink.foodlink.repository.FoodPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodPostService {

    private final FoodPostRepository foodPostRepository;

    public FoodPostService(FoodPostRepository foodPostRepository) {
        this.foodPostRepository = foodPostRepository;
    }

    public List<FoodPost> getAllFoodPosts() {
        return foodPostRepository.findAll();
    }

    public FoodPost getFoodPostById(Long id) {
        return foodPostRepository.findById(id).orElse(null);
    }

    public FoodPost createFoodPost(FoodPost foodPost) {
        return foodPostRepository.save(foodPost);
    }

    public FoodPost updateFoodPost(Long id, FoodPost updatedFoodPost) {

        FoodPost existingFoodPost =
                foodPostRepository.findById(id).orElse(null);

        if (existingFoodPost == null) {
            return null;
        }

        if (existingFoodPost.getStatus() != FoodPostStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING food posts can be updated"
            );
        }

        existingFoodPost.setFoodType(updatedFoodPost.getFoodType());
        existingFoodPost.setQuantity(updatedFoodPost.getQuantity());
        existingFoodPost.setDescription(updatedFoodPost.getDescription());
        existingFoodPost.setPhoto(updatedFoodPost.getPhoto());
        existingFoodPost.setPickupLocation(updatedFoodPost.getPickupLocation());
        existingFoodPost.setAvailableUntil(updatedFoodPost.getAvailableUntil());

        return foodPostRepository.save(existingFoodPost);
    }

    public void deleteFoodPost(Long id) {
        foodPostRepository.deleteById(id);
    }
}