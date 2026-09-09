package com.foodlink.foodlink.dto;

public record GeminiExtractedFoodInfo(
        String foodType,
        int estimatedServings,
        String description,
        double shelfLifeHours,
        UrgencySignal urgencySignal
) {}
