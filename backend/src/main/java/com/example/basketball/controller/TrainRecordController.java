package com.example.basketball.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.basketball.dto.request.TrainRecordRequest;
import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.entity.TrainRecord;
import com.example.basketball.service.TrainRecordService;

@RestController
@RequestMapping("/api/records")
public class TrainRecordController {

    private final TrainRecordService recordService;

    public TrainRecordController(TrainRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TrainRecord>> createRecord(
            @RequestHeader("Authorization") String token,
            @RequestBody TrainRecordRequest request) {
        Long userId = extractUserId(token);
        TrainRecord record = recordService.createRecord(userId, request);
        return ResponseEntity.ok(ApiResponse.success("打卡成功", record));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<TrainRecord>>> getUserRecords(
            @RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        List<TrainRecord> records = recordService.getRecordsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/analyze")
    public ResponseEntity<ApiResponse<String>> analyzeTrainingData(
            @RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        String analysis = recordService.analyzeTrainingData(userId);
        return ResponseEntity.ok(ApiResponse.success(analysis));
    }

    private Long extractUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return 1L;
        }
        throw new RuntimeException("Invalid token");
    }

}
