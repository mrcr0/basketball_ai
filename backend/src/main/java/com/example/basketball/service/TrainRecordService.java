package com.example.basketball.service;

import com.example.basketball.dto.request.TrainRecordRequest;
import com.example.basketball.entity.TrainRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface TrainRecordService {

    TrainRecord createRecord(Long userId, TrainRecordRequest request);

    List<TrainRecord> getRecordsByUserId(Long userId);

    List<TrainRecord> getRecordsByDateRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    String analyzeTrainingData(Long userId);

}
