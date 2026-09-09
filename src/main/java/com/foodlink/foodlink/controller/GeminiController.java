package com.foodlink.foodlink.controller;

import com.foodlink.foodlink.dto.GeminiExtractedFoodInfo;
import com.foodlink.foodlink.dto.GeminiFoodExtractionRequest;
import com.foodlink.foodlink.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping("/test")
    public String testGemini() {

        return geminiService.testGemini(
                "Give me a very short greeting for a food donation platform called FoodLink."
        );
    }

    @PostMapping("/extract-food-post")
    public ResponseEntity<?> extractFoodPost(
            @RequestBody GeminiFoodExtractionRequest request) {

        try {

            if (request == null
                    || request.donorText() == null
                    || request.donorText().isBlank()) {

                return ResponseEntity.badRequest()
                        .body("{\"extracted\":false}");
            }

            String rawResponse =
                    geminiService.extractFoodInfo(
                            request.donorText()
                    );

            GeminiExtractedFoodInfo foodInfo =
                    geminiService.parseFoodInfo(
                            rawResponse
                    );

            return ResponseEntity.ok(foodInfo);

        } catch (Exception e) {

            return ResponseEntity.ok(
                    "{\"extracted\":false}"
            );
        }
    }
}