package com.foodlink.foodlink.service;

public class MatchResult {

    private final Long foodPostId;
    private final Long ngoId;
    private final double cost;

    public MatchResult(
            Long foodPostId,
            Long ngoId,
            double cost) {

        this.foodPostId = foodPostId;
        this.ngoId = ngoId;
        this.cost = cost;
    }

    public Long getFoodPostId() {
        return foodPostId;
    }

    public Long getNgoId() {
        return ngoId;
    }

    public double getCost() {
        return cost;
    }
}
