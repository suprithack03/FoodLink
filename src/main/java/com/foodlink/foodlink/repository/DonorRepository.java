package com.foodlink.foodlink.repository;

import com.foodlink.foodlink.entity.Donor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonorRepository extends JpaRepository<Donor, Long> {
}