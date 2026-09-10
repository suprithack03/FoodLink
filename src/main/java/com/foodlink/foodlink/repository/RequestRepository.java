package com.foodlink.foodlink.repository;

import com.foodlink.foodlink.entity.Request;
import com.foodlink.foodlink.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByNgoId(Long ngoId);

    List<Request> findByNgoIdAndStatusNot(
            Long ngoId,
            RequestStatus status
    );

    List<Request> findByFoodPostId(Long foodPostId);

    List<Request> findByStatus(RequestStatus status);

    boolean existsByNgoIdAndFoodPostId(
            Long ngoId,
            Long foodPostId
    );

    List<Request> findByFoodPostDonorId(Long donorId);

    List<Request> findByFoodPostDonorIdAndStatusNot(
            Long donorId,
            RequestStatus status
    );
}

