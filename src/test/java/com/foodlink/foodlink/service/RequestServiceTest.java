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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private NgoRepository ngoRepository;

    @Mock
    private DonorRepository donorRepository;

    @Mock
    private FoodPostRepository foodPostRepository;

    private RequestService requestService;

    private Ngo ngo;
    private Donor donor;
    private FoodPost foodPost;
    private Request request;

    @BeforeEach
    void setUp() {

        requestService = new RequestService(
                requestRepository,
                ngoRepository,
                donorRepository,
                foodPostRepository
        );

        ngo = new Ngo();
        ngo.setId(1L);
        ngo.setEmail("ngo@example.com");
        ngo.setVerified(true);

        donor = new Donor();
        donor.setId(1L);
        donor.setEmail("donor@example.com");

        foodPost = new FoodPost();
        foodPost.setId(10L);
        foodPost.setDonor(donor);
        foodPost.setStatus(FoodPostStatus.PENDING);

        request = new Request();
        request.setId(100L);
        request.setFoodPost(foodPost);
        request.setNgo(ngo);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void unverifiedNgoCannotCreateRequest() {

        ngo.setVerified(false);

        when(ngoRepository.findByEmail("ngo@example.com"))
                .thenReturn(Optional.of(ngo));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> requestService.createRequest(
                        10L,
                        "ngo@example.com"
                )
        );

        assertEquals(
                "Your NGO account is not verified. You cannot request food yet.",
                exception.getMessage()
        );

        verify(foodPostRepository, never()).findById(anyLong());
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void duplicateRequestIsRejected() {

        when(ngoRepository.findByEmail("ngo@example.com"))
                .thenReturn(Optional.of(ngo));

        when(foodPostRepository.findById(10L))
                .thenReturn(Optional.of(foodPost));

        when(requestRepository.existsByNgoIdAndFoodPostId(1L, 10L))
                .thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> requestService.createRequest(
                        10L,
                        "ngo@example.com"
                )
        );

        assertEquals(
                "You have already requested this food post",
                exception.getMessage()
        );

        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void acceptingRequestMatchesFoodPostAndRejectsOtherPendingRequests() {

        Request otherRequest = new Request();
        otherRequest.setId(101L);
        otherRequest.setFoodPost(foodPost);
        otherRequest.setNgo(ngo);
        otherRequest.setStatus(RequestStatus.PENDING);

        when(requestRepository.findById(100L))
                .thenReturn(Optional.of(request));

        when(requestRepository.findByFoodPostId(10L))
                .thenReturn(List.of(request, otherRequest));

        when(requestRepository.save(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request result = requestService.updateRequestStatus(
                100L,
                RequestStatus.ACCEPTED,
                "donor@example.com"
        );

        assertEquals(
                RequestStatus.ACCEPTED,
                result.getStatus()
        );

        assertEquals(
                FoodPostStatus.MATCHED,
                foodPost.getStatus()
        );

        assertEquals(
                RequestStatus.REJECTED,
                otherRequest.getStatus()
        );

        verify(foodPostRepository).save(foodPost);
        verify(requestRepository).saveAll(List.of(request, otherRequest));
        verify(requestRepository).save(request);
    }

    @Test
    void unauthorizedDonorCannotUpdateRequest() {

        when(requestRepository.findById(100L))
                .thenReturn(Optional.of(request));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> requestService.updateRequestStatus(
                        100L,
                        RequestStatus.ACCEPTED,
                        "another@example.com"
                )
        );

        assertEquals(
                "You are not allowed to update this request",
                exception.getMessage()
        );

        assertEquals(
                RequestStatus.PENDING,
                request.getStatus()
        );

        assertEquals(
                FoodPostStatus.PENDING,
                foodPost.getStatus()
        );

        verify(foodPostRepository, never()).save(any(FoodPost.class));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void completingAcceptedRequestCompletesFoodPost() {

        request.setStatus(RequestStatus.ACCEPTED);
        foodPost.setStatus(FoodPostStatus.MATCHED);

        when(requestRepository.findById(100L))
                .thenReturn(Optional.of(request));

        when(requestRepository.save(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request result = requestService.completeRequest(
                100L,
                "donor@example.com"
        );

        assertEquals(
                RequestStatus.COMPLETED,
                result.getStatus()
        );

        assertEquals(
                FoodPostStatus.COMPLETED,
                foodPost.getStatus()
        );

        verify(foodPostRepository).save(foodPost);
        verify(requestRepository).save(request);
    }
}

