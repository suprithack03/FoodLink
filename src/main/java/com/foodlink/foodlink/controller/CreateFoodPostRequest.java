package com.foodlink.foodlink.controller;

import java.time.LocalDateTime;

public class CreateFoodPostRequest {

    private String foodType;

    private Integer quantity;

    private String description;

    private String photo;

    private String pickupLocation;

    private Double latitude;

    private Double longitude;

    private LocalDateTime availableUntil;

    private Double shelfLifeHours;

    public CreateFoodPostRequest() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(LocalDateTime availableUntil) {
        this.availableUntil = availableUntil;
    }

    public Double getShelfLifeHours() {
        return shelfLifeHours;
    }

    public void setShelfLifeHours(Double shelfLifeHours) {
        this.shelfLifeHours = shelfLifeHours;
    }
}