package com.foodlink.foodlink.repository;

import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.entity.FoodPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FoodPostRepository extends JpaRepository<FoodPost, Long> {

    List<FoodPost> findByStatusAndAvailableUntilLessThanEqual(
            FoodPostStatus status,
            LocalDateTime time
    );
}