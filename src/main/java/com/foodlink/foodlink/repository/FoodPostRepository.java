package com.foodlink.foodlink.repository;

import com.foodlink.foodlink.entity.FoodPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodPostRepository extends JpaRepository<FoodPost, Long> {
}