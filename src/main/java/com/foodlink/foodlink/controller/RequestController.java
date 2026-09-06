package com.foodlink.foodlink.controller;

import com.foodlink.foodlink.dto.UpdateRequestStatusRequest;
import com.foodlink.foodlink.entity.Request;
import com.foodlink.foodlink.entity.RequestStatus;
import com.foodlink.foodlink.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public List<Request> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{id}")
    public Request getRequestById(@PathVariable Long id) {
        return requestService.getRequestById(id);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyRequests(
            Authentication authentication) {

        try {
            String email = authentication.getName();

            List<Request> requests =
                    requestService.getRequestsByNgo(email);

            return ResponseEntity.ok(requests);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/my-food-posts")
    public ResponseEntity<?> getMyFoodPostRequests(
            Authentication authentication) {

        try {
            String email = authentication.getName();

            List<Request> requests =
                    requestService.getRequestsByDonor(email);

            return ResponseEntity.ok(requests);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createRequest(
            @RequestBody CreateRequestRequest request,
            Authentication authentication) {

        try {
            String email = authentication.getName();

            Request createdRequest =
                    requestService.createRequest(
                            request.getFoodPostId(),
                            email
                    );

            return ResponseEntity.ok(createdRequest);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody UpdateRequestStatusRequest request,
            Authentication authentication) {

        try {
            String email = authentication.getName();

            Request updatedRequest;

            /*
             * COMPLETED is handled separately because completing
             * a request must also complete its FoodPost.
             */
            if (request.getStatus() == RequestStatus.COMPLETED) {

                updatedRequest =
                        requestService.completeRequest(
                                id,
                                email
                        );

            } else {

                /*
                 * Handles:
                 * PENDING -> ACCEPTED
                 * PENDING -> REJECTED
                 */
                updatedRequest =
                        requestService.updateRequestStatus(
                                id,
                                request.getStatus(),
                                email
                        );
            }

            return ResponseEntity.ok(updatedRequest);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Request updateRequest(
            @PathVariable Long id,
            @RequestBody Request updatedRequest) {

        Request existingRequest =
                requestService.getRequestById(id);

        if (existingRequest == null) {
            return null;
        }

        existingRequest.setFoodPost(updatedRequest.getFoodPost());
        existingRequest.setNgo(updatedRequest.getNgo());
        existingRequest.setStatus(updatedRequest.getStatus());
        existingRequest.setCreatedAt(updatedRequest.getCreatedAt());

        return requestService.updateRequest(existingRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
    }
}