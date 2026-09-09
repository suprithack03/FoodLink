package com.foodlink.foodlink.controller;

public class MatchResponse {


private final Long foodPostId;
private final Long ngoId;
private final double cost;

public MatchResponse(
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
