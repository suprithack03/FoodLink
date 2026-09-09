package com.foodlink.foodlink.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodlink.foodlink.dto.GeminiExtractedFoodInfo;
import com.google.genai.Client;
import com.google.genai.gaos.models.interactions.Content;
import com.google.genai.gaos.models.interactions.CreateModelInteraction;
import com.google.genai.gaos.models.interactions.InteractionsInput;
import com.google.genai.gaos.models.interactions.Model;
import com.google.genai.gaos.models.interactions.ModelOutputStep;
import com.google.genai.gaos.models.interactions.Step;
import com.google.genai.gaos.models.operations.CreateInteractionRequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;
    private final ObjectMapper objectMapper;

    private static final String FOOD_EXTRACTION_INSTRUCTION = """
            You are a data extraction system for FoodLink, a food donation platform.
            Your only task is to read a donor's free-text description of surplus food
            and extract structured information from it. You are not a conversational
            assistant and must never behave like one.

            Output ONLY a single JSON object with exactly these five fields, in this order, and no others:

            {
              "foodType": string,
              "estimatedServings": integer,
              "description": string,
              "shelfLifeHours": number,
              "urgencySignal": "LOW" | "MEDIUM" | "HIGH"
            }

            Rules:
            - Output raw JSON only. No markdown, no code fences, no prose before or after,
              no explanation, no apology, no follow-up questions.
            - "foodType": a short non-empty description of the food itself.
            - "estimatedServings": a positive integer. If the donor gives a range,
              use your best single-number estimate.
            - "description": one concise sentence summarizing the donor's input in
              your own words. Do not add facts the donor did not state.
            - "shelfLifeHours": a positive number of hours representing how long this
              food is safely edible from now, based on what the donor said and ordinary
              food-safety judgment for that food type. Never exceed 72.
            - "urgencySignal": your qualitative read of how time-pressured the donor's
              own wording sounds — LOW, MEDIUM, or HIGH. This is advisory only and is
              never used as a precise number by the system.

            If the donor's input does not describe an actual food donation, is too vague
            to responsibly estimate servings or shelf life, or does not contain enough
            information to fill every field in good faith, do not guess or invent values.
            Instead output exactly:

            {
              "foodType": null,
              "estimatedServings": null,
              "description": null,
              "shelfLifeHours": null,
              "urgencySignal": null
            }

            Never fabricate specific numbers (servings, hours) you cannot reasonably infer
            from the text. It is always better to return nulls than to guess.

            The urgencySignal is advisory only and must never be used as the numeric urgency
            score used by FoodLink's deterministic urgency scoring or matching algorithm.

            Gemini must not generate or modify donorId, id, postedAt, status, pickupLocation,
            or photo. Gemini must not create or save a FoodPost.
            """;

    public GeminiService(
            @Value("${gemini.api.key}") String apiKey,
            ObjectMapper objectMapper) {

        this.client = Client.builder()
                .apiKey(apiKey)
                .build();

        this.objectMapper = objectMapper;
    }

    public String testGemini(String prompt) {

        CreateModelInteraction request =
                CreateModelInteraction.builder()
                        .model(Model.of("gemini-3.6-flash"))
                        .input(InteractionsInput.of(prompt))
                        .build();

        var interaction =
                client.interactions
                        .create(
                                CreateInteractionRequestBody.of(request)
                        )
                        .interaction()
                        .get();

        var stepsOptional = interaction.steps();

        if (stepsOptional.isPresent()) {

            for (Step step : stepsOptional.get()) {

                if (step instanceof ModelOutputStep outputStep) {

                    var contentOptional = outputStep.content();

                    if (contentOptional.isPresent()) {

                        for (Content content : contentOptional.get()) {

                            if (content instanceof com.google.genai.gaos.models.interactions.TextContent textContent) {

                                return textContent.text().orElse("");
                            }
                        }
                    }
                }
            }
        }

        return "";
    }

    public String extractFoodInfo(String donorText) {

        CreateModelInteraction request =
                CreateModelInteraction.builder()
                        .model(Model.of("gemini-3.6-flash"))
                        .systemInstruction(FOOD_EXTRACTION_INSTRUCTION)
                        .input(InteractionsInput.of(donorText))
                        .build();

        var interaction =
                client.interactions
                        .create(
                                CreateInteractionRequestBody.of(request)
                        )
                        .interaction()
                        .get();

        var stepsOptional = interaction.steps();

        if (stepsOptional.isPresent()) {

            for (Step step : stepsOptional.get()) {

                if (step instanceof ModelOutputStep outputStep) {

                    var contentOptional = outputStep.content();

                    if (contentOptional.isPresent()) {

                        for (Content content : contentOptional.get()) {

                            if (content instanceof com.google.genai.gaos.models.interactions.TextContent textContent) {

                                return textContent.text().orElse("");
                            }
                        }
                    }
                }
            }
        }

        return "";
    }

    public GeminiExtractedFoodInfo parseFoodInfo(String json) {

        try {

            GeminiExtractedFoodInfo foodInfo =
                    objectMapper.readValue(
                            json,
                            GeminiExtractedFoodInfo.class
                    );

            if (foodInfo.foodType() == null
                    || foodInfo.foodType().isBlank()) {

                throw new IllegalArgumentException(
                        "foodType is missing"
                );
            }

            if (foodInfo.estimatedServings() <= 0) {

                throw new IllegalArgumentException(
                        "estimatedServings must be greater than 0"
                );
            }

            if (foodInfo.description() == null
                    || foodInfo.description().isBlank()) {

                throw new IllegalArgumentException(
                        "description is missing"
                );
            }

            if (foodInfo.shelfLifeHours() <= 0
                    || foodInfo.shelfLifeHours() > 72) {

                throw new IllegalArgumentException(
                        "shelfLifeHours must be greater than 0 and no more than 72"
                );
            }

            if (foodInfo.urgencySignal() == null) {

                throw new IllegalArgumentException(
                        "urgencySignal is missing"
                );
            }

            return foodInfo;

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Invalid Gemini food extraction response",
                    e
            );
        }
    }
}