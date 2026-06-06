package com.example.basketball.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.basketball.ai.DeepSeekClient;
import com.example.basketball.dto.request.TrainRecordRequest;
import com.example.basketball.entity.TrainRecord;
import com.example.basketball.mapper.TrainRecordMapper;
import com.example.basketball.service.TrainRecordService;

@Service
public class TrainRecordServiceImpl implements TrainRecordService {

    private static final Logger log = LoggerFactory.getLogger(TrainRecordServiceImpl.class);

    private final TrainRecordMapper recordMapper;
    private final DeepSeekClient deepSeekClient;

    public TrainRecordServiceImpl(TrainRecordMapper recordMapper, DeepSeekClient deepSeekClient) {
        this.recordMapper = recordMapper;
        this.deepSeekClient = deepSeekClient;
    }

    @Override
    @Transactional
    public TrainRecord createRecord(Long userId, TrainRecordRequest request) {
        TrainRecord record = new TrainRecord();
        record.setUserId(userId);
        record.setPlanId(request.getPlanId());
        record.setTrainingItem(request.getTrainingItem());
        record.setHitRate(request.getHitRate());
        record.setDuration(request.getDuration());
        record.setIntensity(request.getIntensity());
        record.setCalories(request.getCalories());
        record.setCompletionRate(request.getCompletionRate());
        record.setNotes(request.getNotes());
        record.setCheckInTime(LocalDateTime.now());

        recordMapper.insert(record);
        log.info("训练记录创建成功: userId={}, recordId={}", userId, record.getId());
        return record;
    }

    @Override
    public List<TrainRecord> getRecordsByUserId(Long userId) {
        return recordMapper.selectByUserId(userId);
    }

    @Override
    public List<TrainRecord> getRecordsByDateRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return recordMapper.selectByUserIdAndDateRange(userId, startTime, endTime);
    }

    @Override
    public String analyzeTrainingData(Long userId) {
        List<TrainRecord> records = recordMapper.selectByUserId(userId);
        if (records.isEmpty()) {
            throw new RuntimeException("暂无训练数据");
        }

        JSONObject dataJson = new JSONObject();
        JSONArray recordsArray = new JSONArray();

        for (TrainRecord record : records) {
            JSONObject recordObj = new JSONObject();
            recordObj.put("trainingItem", record.getTrainingItem());
            recordObj.put("hitRate", record.getHitRate());
            recordObj.put("duration", record.getDuration());
            recordObj.put("intensity", record.getIntensity());
            recordObj.put("calories", record.getCalories());
            recordObj.put("completionRate", record.getCompletionRate());
            recordObj.put("checkInTime", record.getCheckInTime().toString());
            recordsArray.add(recordObj);
        }

        dataJson.put("records", recordsArray);
        dataJson.put("totalCount", records.size());

        return deepSeekClient.analyzeTrainingData(dataJson.toJSONString());
    }

}
