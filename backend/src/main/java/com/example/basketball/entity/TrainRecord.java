package com.example.basketball.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("train_record")
public class TrainRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("plan_id")
    private Long planId;

    @TableField("training_item")
    private String trainingItem;

    @TableField("hit_rate")
    private BigDecimal hitRate;

    @TableField("duration")
    private Integer duration;

    @TableField("intensity")
    private String intensity;

    @TableField("calories")
    private BigDecimal calories;

    @TableField("completion_rate")
    private BigDecimal completionRate;

    @TableField("notes")
    private String notes;

    @TableField("check_in_time")
    private LocalDateTime checkInTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public String getTrainingItem() { return trainingItem; }
    public void setTrainingItem(String trainingItem) { this.trainingItem = trainingItem; }
    public BigDecimal getHitRate() { return hitRate; }
    public void setHitRate(BigDecimal hitRate) { this.hitRate = hitRate; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }
    public BigDecimal getCalories() { return calories; }
    public void setCalories(BigDecimal calories) { this.calories = calories; }
    public BigDecimal getCompletionRate() { return completionRate; }
    public void setCompletionRate(BigDecimal completionRate) { this.completionRate = completionRate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }
}
