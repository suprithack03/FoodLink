package com.foodlink.foodlink.dto;

import com.foodlink.foodlink.entity.RequestStatus;

public class UpdateRequestStatusRequest {

    private RequestStatus status;

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
