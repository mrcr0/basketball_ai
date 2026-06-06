package com.example.basketball.dto.request;

import java.math.BigDecimal;

public class TrainRecordRequest {

    private Long planId;

    private String trainingItem;

    private BigDecimal hitRate;

    private Integer duration;

    private String intensity;

    private BigDecimal calories;

    private BigDecimal completionRate;

    private String notes;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getTrainingItem() {
        return trainingItem;
    }

    public void setTrainingItem(String trainingItem) {
        this.trainingItem = trainingItem;
    }

    public BigDecimal getHitRate() {
        return hitRate;
    }

    public void setHitRate(BigDecimal hitRate) {
        this.hitRate = hitRate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public BigDecimal getCalories() {
        return calories;
    }

    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    public BigDecimal getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(BigDecimal completionRate) {
        this.completionRate = completionRate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
