package com.example.basketball.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.basketball.ai.DeepSeekClient;
import com.example.basketball.dto.request.AIPlanRequest;
import com.example.basketball.entity.BasketballPlan;
import com.example.basketball.entity.User;
import com.example.basketball.mapper.BasketballPlanMapper;
import com.example.basketball.mapper.UserMapper;
import com.example.basketball.service.BasketballPlanService;

@Service
public class BasketballPlanServiceImpl implements BasketballPlanService {

    private static final Logger log = LoggerFactory.getLogger(BasketballPlanServiceImpl.class);

    private final BasketballPlanMapper planMapper;
    private final UserMapper userMapper;
    private final DeepSeekClient deepSeekClient;

    public BasketballPlanServiceImpl(BasketballPlanMapper planMapper, UserMapper userMapper, DeepSeekClient deepSeekClient) {
        this.planMapper = planMapper;
        this.userMapper = userMapper;
        this.deepSeekClient = deepSeekClient;
    }

    @Override
    @Transactional
    public BasketballPlan generateAIPlan(Long userId, AIPlanRequest request) {
        log.info("开始生成AI训练计划 - userId: {}, request: {}", userId, request);

        User user = null;
        if (userId != null && userId > 0) {
            user = userMapper.selectById(userId);
            log.info("找到用户: {}", user != null ? user.getId() : "null");
        }

        String position = request.getPosition() != null ? request.getPosition() : (user != null ? user.getPosition() : null);
        String skillLevel = request.getSkillLevel() != null ? request.getSkillLevel() : (user != null ? user.getSkillLevel() : null);
        String weakPoints = request.getWeakPoints() != null ? request.getWeakPoints() : (user != null ? user.getWeakPoints() : null);
        String trainingGoal = request.getTrainingGoal() != null ? request.getTrainingGoal() : (user != null ? user.getTrainingGoal() : null);
        String height = request.getHeight() != null ? request.getHeight().toString() : (user != null && user.getHeight() != null ? user.getHeight().toString() : "");
        String weight = request.getWeight() != null ? request.getWeight().toString() : (user != null && user.getWeight() != null ? user.getWeight().toString() : "");
        String experienceYears = request.getExperienceYears() != null ? request.getExperienceYears().toString() : (user != null && user.getExperienceYears() != null ? user.getExperienceYears().toString() : "0");
        String cycleType = request.getCycleType() != null ? request.getCycleType() : "周";
        int cycleCount = request.getCycleCount() != null ? request.getCycleCount() : 1;

        log.info("生成AI训练计划参数 - position: {}, skillLevel: {}, weakPoints: {}, trainingGoal: {}, height: {}, weight: {}, experienceYears: {}, cycleType: {}, cycleCount: {}",
                position, skillLevel, weakPoints, trainingGoal, height, weight, experienceYears, cycleType, cycleCount);

        try {
            String aiContent = deepSeekClient.generateTrainingPlan(
                    position != null ? position : "未指定",
                    skillLevel != null ? skillLevel : "新手",
                    weakPoints != null ? weakPoints : "无",
                    trainingGoal != null ? trainingGoal : "提升技术",
                    height,
                    weight,
                    experienceYears,
                    cycleType,
                    cycleCount
            );

            log.info("AI训练计划生成成功，内容长度: {}", aiContent != null ? aiContent.length() : 0);

            BasketballPlan plan = new BasketballPlan();
            plan.setUserId(userId);
            plan.setPlanName(cycleType + "训练计划");
            plan.setTrainingCycle(cycleType);
            plan.setCycleCount(cycleCount);
            plan.setAiContent(aiContent);
            plan.setSpecialFocus(weakPoints);
            plan.setIsCustomized(0);
            plan.setStatus(1);

            planMapper.insert(plan);
            log.info("AI训练计划生成成功: userId={}, planId={}", userId, plan.getId());
            return plan;
        } catch (Exception e) {
            log.error("生成AI训练计划失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成训练计划失败: " + e.getMessage(), e);
        }
    }

    @Override
    public BasketballPlan getPlanById(Long planId) {
        BasketballPlan plan = planMapper.selectById(planId);
        if (plan == null) {
            throw new RuntimeException("训练计划不存在");
        }
        return plan;
    }

    @Override
    public List<BasketballPlan> getPlansByUserId(Long userId) {
        return planMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public BasketballPlan updatePlan(Long planId, String aiContent) {
        BasketballPlan plan = planMapper.selectById(planId);
        if (plan == null) {
            throw new RuntimeException("训练计划不存在");
        }

        plan.setAiContent(aiContent);
        plan.setIsCustomized(1);
        planMapper.updateById(plan);
        log.info("训练计划更新成功: {}", planId);
        return plan;
    }

    @Override
    @Transactional
    public void deletePlan(Long planId) {
        BasketballPlan plan = planMapper.selectById(planId);
        if (plan == null) {
            throw new RuntimeException("训练计划不存在");
        }
        planMapper.deleteById(planId);
        log.info("训练计划删除成功: {}", planId);
    }

}
