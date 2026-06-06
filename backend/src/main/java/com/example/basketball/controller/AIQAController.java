package com.example.basketball.controller;

import com.example.basketball.ai.DeepSeekClient;
import com.example.basketball.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIQAController {

    private final DeepSeekClient deepSeekClient;

    public AIQAController(DeepSeekClient deepSeekClient) {
        this.deepSeekClient = deepSeekClient;
    }

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<String>> askQuestion(@RequestBody String question) {
        String answer = deepSeekClient.answerQuestion(question);
        return ResponseEntity.ok(ApiResponse.success(answer));
    }

    @PostMapping("/analyze-action")
    public ResponseEntity<ApiResponse<String>> analyzeAction(
            @RequestParam String actionType,
            @RequestBody String description) {
        String analysis = deepSeekClient.analyzeAction(actionType, description);
        return ResponseEntity.ok(ApiResponse.success(analysis));
    }

}
