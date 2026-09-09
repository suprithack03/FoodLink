package com.foodlink.foodlink.controller;

public class GeminiFoodResponse {

    private String foodType;

    private Integer quantity;

    private Integer servings;

    private String urgencySignal;

    public GeminiFoodResponse() {
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getUrgencySignal() {
        return urgencySignal;
    }

    public void setUrgencySignal(String urgencySignal) {
        this.urgencySignal = urgencySignal;
    }
}


