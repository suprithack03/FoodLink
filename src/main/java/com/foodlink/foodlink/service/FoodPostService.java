package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.Donor;
import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.entity.FoodPostStatus;
import com.foodlink.foodlink.repository.DonorRepository;
import com.foodlink.foodlink.repository.FoodPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodPostService {

    private final FoodPostRepository foodPostRepository;
    private final DonorRepository donorRepository;

    public FoodPostService(
            FoodPostRepository foodPostRepository,
            DonorRepository donorRepository) {

        this.foodPostRepository = foodPostRepository;
        this.donorRepository = donorRepository;
    }

    public List<FoodPost> getAllFoodPosts() {
        return foodPostRepository.findAll();
    }

    public FoodPost getFoodPostById(Long id) {
        return foodPostRepository.findById(id).orElse(null);
    }

    public FoodPost createFoodPost(FoodPost foodPost, String email) {

        Donor donor = donorRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated donor not found"
                        ));

        foodPost.setDonor(donor);
        foodPost.setStatus(FoodPostStatus.PENDING);

        return foodPostRepository.save(foodPost);
    }

    public FoodPost updateFoodPost(
            Long id,
            FoodPost updatedFoodPost,
            String email) {

        FoodPost existingFoodPost =
                foodPostRepository.findById(id).orElse(null);

        if (existingFoodPost == null) {
            return null;
        }

        if (!existingFoodPost.getDonor().getEmail().equals(email)) {
            throw new IllegalStateException(
                    "You are not allowed to update this food post"
            );
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
        existingFoodPost.setAvailableUntil(
                updatedFoodPost.getAvailableUntil()
        );

        return foodPostRepository.save(existingFoodPost);
    }

    public void cancelFoodPost(Long id, String email) {

        FoodPost existingFoodPost =
                foodPostRepository.findById(id).orElse(null);

        if (existingFoodPost == null) {
            throw new IllegalStateException(
                    "Food post not found"
            );
        }

        if (!existingFoodPost.getDonor().getEmail().equals(email)) {
            throw new IllegalStateException(
                    "You are not allowed to cancel this food post"
            );
        }

        if (existingFoodPost.getStatus() != FoodPostStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING food posts can be cancelled"
            );
        }

        existingFoodPost.setStatus(FoodPostStatus.CANCELLED);

        foodPostRepository.save(existingFoodPost);
    }
}