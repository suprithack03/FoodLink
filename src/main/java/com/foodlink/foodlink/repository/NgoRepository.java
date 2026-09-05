package com.foodlink.foodlink.repository;

import com.foodlink.foodlink.entity.Ngo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NgoRepository extends JpaRepository<Ngo, Long> {

    Optional<Ngo> findByEmail(String email);
}