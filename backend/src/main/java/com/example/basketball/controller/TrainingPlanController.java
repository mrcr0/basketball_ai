package com.example.basketball.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.basketball.dto.request.AIPlanRequest;
import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.entity.BasketballPlan;
import com.example.basketball.service.BasketballPlanService;

@RestController
@RequestMapping("/api/plans")
public class TrainingPlanController {

    private static final Long ANONYMOUS_USER_ID = 0L;

    private final BasketballPlanService planService;

    public TrainingPlanController(BasketballPlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/ai-generate")
    public ResponseEntity<ApiResponse<BasketballPlan>> generateAIPlan(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody AIPlanRequest request) {
        Long userId = extractUserId(token);
        BasketballPlan plan = planService.generateAIPlan(userId, request);
        return ResponseEntity.ok(ApiResponse.success("训练计划生成成功", plan));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<ApiResponse<BasketballPlan>> getPlan(@PathVariable Long planId) {
        BasketballPlan plan = planService.getPlanById(planId);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<BasketballPlan>>> getUserPlans(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        List<BasketballPlan> plans = planService.getPlansByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @PutMapping("/{planId}")
    public ResponseEntity<ApiResponse<BasketballPlan>> updatePlan(
            @PathVariable Long planId,
            @RequestBody String aiContent) {
        BasketballPlan plan = planService.updatePlan(planId, aiContent);
        return ResponseEntity.ok(ApiResponse.success("更新成功", plan));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<ApiResponse<Void>> deletePlan(@PathVariable Long planId) {
        planService.deletePlan(planId);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    private Long extractUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            try {
                return 1L;
            } catch (Exception e) {
                return ANONYMOUS_USER_ID;
            }
        }
        return ANONYMOUS_USER_ID;
    }

}
