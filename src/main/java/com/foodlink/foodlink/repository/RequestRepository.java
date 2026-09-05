package com.foodlink.foodlink.repository;

import com.foodlink.foodlink.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
}