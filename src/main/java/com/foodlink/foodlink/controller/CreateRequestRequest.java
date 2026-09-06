package com.foodlink.foodlink.controller;

public class CreateRequestRequest {

    private Long foodPostId;

    public CreateRequestRequest() {
    }

    public Long getFoodPostId() {
        return foodPostId;
    }

    public void setFoodPostId(Long foodPostId) {
        this.foodPostId = foodPostId;
    }
}