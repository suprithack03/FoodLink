package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.Donor;
import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.entity.FoodPostStatus;
import com.foodlink.foodlink.entity.Request;
import com.foodlink.foodlink.entity.RequestStatus;
import com.foodlink.foodlink.repository.DonorRepository;
import com.foodlink.foodlink.repository.FoodPostRepository;
import com.foodlink.foodlink.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FoodPostService {

    private final FoodPostRepository foodPostRepository;
    private final DonorRepository donorRepository;
    private final RequestRepository requestRepository;

    public FoodPostService(
            FoodPostRepository foodPostRepository,
            DonorRepository donorRepository,
            RequestRepository requestRepository) {

        this.foodPostRepository = foodPostRepository;
        this.donorRepository = donorRepository;
        this.requestRepository = requestRepository;
    }

    public List<FoodPost> getAllFoodPosts() {
        return foodPostRepository.findAll();
    }

    public List<FoodPost> getAvailableFoodPosts() {
        return foodPostRepository.findByStatusAndAvailableUntilGreaterThan(
                FoodPostStatus.PENDING,
                LocalDateTime.now()
        );
    }

    public FoodPost getFoodPostById(Long id) {
        return foodPostRepository.findById(id).orElse(null);
    }

    public List<FoodPost> getFoodPostsByDonor(String email) {
        return foodPostRepository.findByDonorEmail(email);
    }

    public FoodPost createFoodPost(
            FoodPost foodPost,
            String email) {

        Donor donor = donorRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated donor not found"
                        ));

        LocalDateTime postedAt = LocalDateTime.now();

        foodPost.setDonor(donor);
        foodPost.setPostedAt(postedAt);
        foodPost.setStatus(FoodPostStatus.PENDING);

        if (foodPost.getShelfLifeHours() == null ||
                foodPost.getShelfLifeHours() <= 0) {

            throw new IllegalStateException(
                    "Shelf life must be greater than 0 hours"
            );
        }

        foodPost.setAvailableUntil(
                postedAt.plusMinutes(
                        Math.round(foodPost.getShelfLifeHours() * 60)
                )
        );

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
        existingFoodPost.setPickupLocation(
                updatedFoodPost.getPickupLocation()
        );

        existingFoodPost.setLatitude(updatedFoodPost.getLatitude());
        existingFoodPost.setLongitude(updatedFoodPost.getLongitude());

        if (updatedFoodPost.getShelfLifeHours() == null ||
                updatedFoodPost.getShelfLifeHours() <= 0) {

            throw new IllegalStateException(
                    "Shelf life must be greater than 0 hours"
            );
        }

        existingFoodPost.setShelfLifeHours(
                updatedFoodPost.getShelfLifeHours()
        );

        LocalDateTime postedAt = existingFoodPost.getPostedAt();

        if (postedAt == null) {
            postedAt = LocalDateTime.now();
            existingFoodPost.setPostedAt(postedAt);
        }

        existingFoodPost.setAvailableUntil(
                postedAt.plusMinutes(
                        Math.round(
                                updatedFoodPost.getShelfLifeHours() * 60
                        )
                )
        );

        return foodPostRepository.save(existingFoodPost);
    }

    @Transactional
    public void cancelFoodPost(Long id, String email) {

        FoodPost existingFoodPost =
                foodPostRepository.findById(id).orElse(null);

        if (existingFoodPost == null) {
            throw new IllegalStateException("Food post not found");
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

        List<Request> requests =
                requestRepository.findByFoodPostId(id);

        for (Request request : requests) {
            if (request.getStatus() == RequestStatus.PENDING) {
                request.setStatus(RequestStatus.REJECTED);
            }
        }

        foodPostRepository.save(existingFoodPost);
        requestRepository.saveAll(requests);
    }
}