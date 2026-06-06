package com.example.basketball.service;

import com.example.basketball.dto.request.AIPlanRequest;
import com.example.basketball.entity.BasketballPlan;

import java.util.List;

public interface BasketballPlanService {

    BasketballPlan generateAIPlan(Long userId, AIPlanRequest request);

    BasketballPlan getPlanById(Long planId);

    List<BasketballPlan> getPlansByUserId(Long userId);

    BasketballPlan updatePlan(Long planId, String aiContent);

    void deletePlan(Long planId);

}
