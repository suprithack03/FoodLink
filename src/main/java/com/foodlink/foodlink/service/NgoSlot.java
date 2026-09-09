package com.foodlink.foodlink.service;

public class NgoSlot {

    private final Long ngoId;
    private final double latitude;
    private final double longitude;

    public NgoSlot(
            Long ngoId,
            double latitude,
            double longitude) {

        this.ngoId = ngoId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getNgoId() {
        return ngoId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
