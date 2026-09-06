package com.foodlink.foodlink.service;

import com.foodlink.foodlink.entity.Donor;
import com.foodlink.foodlink.entity.FoodPost;
import com.foodlink.foodlink.entity.FoodPostStatus;
import com.foodlink.foodlink.entity.Ngo;
import com.foodlink.foodlink.entity.Request;
import com.foodlink.foodlink.entity.RequestStatus;
import com.foodlink.foodlink.repository.DonorRepository;
import com.foodlink.foodlink.repository.FoodPostRepository;
import com.foodlink.foodlink.repository.NgoRepository;
import com.foodlink.foodlink.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final NgoRepository ngoRepository;
    private final DonorRepository donorRepository;
    private final FoodPostRepository foodPostRepository;

    public RequestService(
            RequestRepository requestRepository,
            NgoRepository ngoRepository,
            DonorRepository donorRepository,
            FoodPostRepository foodPostRepository) {

        this.requestRepository = requestRepository;
        this.ngoRepository = ngoRepository;
        this.donorRepository = donorRepository;
        this.foodPostRepository = foodPostRepository;
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Request getRequestById(Long id) {
        return requestRepository.findById(id).orElse(null);
    }

    public List<Request> getRequestsByNgo(String email) {

        Ngo ngo = ngoRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated NGO not found"
                        ));

        return requestRepository.findByNgoId(ngo.getId());
    }

    public List<Request> getRequestsByDonor(String email) {

        Donor donor = donorRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated donor not found"
                        ));

        return requestRepository.findByFoodPostDonorId(donor.getId());
    }

    public Request createRequest(Long foodPostId, String email) {

        Ngo ngo = ngoRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated NGO not found"
                        ));

        FoodPost foodPost = foodPostRepository.findById(foodPostId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Food post not found"
                        ));

        if (foodPost.getStatus() != FoodPostStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING food posts can be requested"
            );
        }

        if (requestRepository.existsByNgoIdAndFoodPostId(
                ngo.getId(),
                foodPostId)) {

            throw new IllegalStateException(
                    "You have already requested this food post"
            );
        }

        Request request = new Request();

        request.setFoodPost(foodPost);
        request.setNgo(ngo);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    public Request updateRequest(Request request) {
        return requestRepository.save(request);
    }

    @Transactional
    public Request updateRequestStatus(
            Long requestId,
            RequestStatus newStatus,
            String email) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Request not found"
                        ));

        FoodPost foodPost = request.getFoodPost();

        // Verify that the authenticated user owns the FoodPost
        if (!foodPost.getDonor().getEmail().equals(email)) {
            throw new IllegalStateException(
                    "You are not allowed to update this request"
            );
        }

        // Request must still be PENDING
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING requests can be updated"
            );
        }

        // FoodPost must still be PENDING
        if (foodPost.getStatus() != FoodPostStatus.PENDING) {
            throw new IllegalStateException(
                    "Only requests for PENDING food posts can be updated"
            );
        }

        // Only ACCEPTED or REJECTED are allowed here
        if (newStatus != RequestStatus.ACCEPTED &&
                newStatus != RequestStatus.REJECTED) {

            throw new IllegalStateException(
                    "Request can only be ACCEPTED or REJECTED"
            );
        }

        // Donor rejects this request
        if (newStatus == RequestStatus.REJECTED) {

            request.setStatus(RequestStatus.REJECTED);

            return requestRepository.save(request);
        }

        // Donor accepts this request
        request.setStatus(RequestStatus.ACCEPTED);

        // The FoodPost is now matched with this NGO
        foodPost.setStatus(FoodPostStatus.MATCHED);

        foodPostRepository.save(foodPost);

        // Automatically reject all other pending requests
        List<Request> otherRequests =
                requestRepository.findByFoodPostId(foodPost.getId());

        for (Request otherRequest : otherRequests) {

            if (!otherRequest.getId().equals(request.getId()) &&
                    otherRequest.getStatus() == RequestStatus.PENDING) {

                otherRequest.setStatus(RequestStatus.REJECTED);
            }
        }

        requestRepository.saveAll(otherRequests);

        return requestRepository.save(request);
    }

    @Transactional
    public Request completeRequest(
            Long requestId,
            String email) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Request not found"
                        ));

        FoodPost foodPost = request.getFoodPost();

        // Only the donor who owns the FoodPost can confirm completion
        if (!foodPost.getDonor().getEmail().equals(email)) {
            throw new IllegalStateException(
                    "You are not allowed to complete this request"
            );
        }

        // Request must be ACCEPTED
        if (request.getStatus() != RequestStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Only ACCEPTED requests can be completed"
            );
        }

        // FoodPost must be MATCHED
        if (foodPost.getStatus() != FoodPostStatus.MATCHED) {
            throw new IllegalStateException(
                    "Only MATCHED food posts can be completed"
            );
        }

        // Complete both sides of the relationship
        request.setStatus(RequestStatus.COMPLETED);
        foodPost.setStatus(FoodPostStatus.COMPLETED);

        foodPostRepository.save(foodPost);

        return requestRepository.save(request);
    }

    /*
     * Expire a FoodPost whose availableUntil time has passed.
     *
     * Rules:
     * - Only PENDING FoodPosts can expire.
     * - The FoodPost becomes EXPIRED.
     * - Any remaining PENDING Requests become REJECTED.
     * - Everything happens inside one transaction.
     */
    @Transactional
    public void expireFoodPost(Long foodPostId) {

        FoodPost foodPost = foodPostRepository.findById(foodPostId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Food post not found"
                        ));

        // Only PENDING posts can become EXPIRED
        if (foodPost.getStatus() != FoodPostStatus.PENDING) {
            return;
        }

        // The post can expire only after its availableUntil time
        if (foodPost.getAvailableUntil() == null ||
                foodPost.getAvailableUntil().isAfter(LocalDateTime.now())) {

            return;
        }

        // Change FoodPost status
        foodPost.setStatus(FoodPostStatus.EXPIRED);

        // Reject all remaining pending requests
        List<Request> requests =
                requestRepository.findByFoodPostId(foodPostId);

        for (Request request : requests) {

            if (request.getStatus() == RequestStatus.PENDING) {
                request.setStatus(RequestStatus.REJECTED);
            }
        }

        foodPostRepository.save(foodPost);
        requestRepository.saveAll(requests);
    }

    public void deleteRequest(Long id) {
        requestRepository.deleteById(id);
    }
}