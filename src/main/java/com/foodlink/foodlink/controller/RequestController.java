package com.foodlink.foodlink.controller;

import com.foodlink.foodlink.entity.Request;
import com.foodlink.foodlink.service.RequestService;
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

    @PostMapping
    public Request createRequest(@RequestBody Request request) {
        return requestService.createRequest(request);
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