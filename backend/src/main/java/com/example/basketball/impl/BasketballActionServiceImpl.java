package com.example.basketball.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.basketball.entity.BasketballAction;
import com.example.basketball.mapper.BasketballActionMapper;
import com.example.basketball.service.BasketballActionService;

@Service
public class BasketballActionServiceImpl implements BasketballActionService {

    private static final Logger log = LoggerFactory.getLogger(BasketballActionServiceImpl.class);

    private final BasketballActionMapper actionMapper;

    public BasketballActionServiceImpl(BasketballActionMapper actionMapper) {
        this.actionMapper = actionMapper;
    }

    @Override
    public List<BasketballAction> getAllActions() {
        return actionMapper.selectList(null);
    }

    @Override
    public List<BasketballAction> getActionsByCategory(String category) {
        return actionMapper.selectByCategory(category);
    }

    @Override
    public List<BasketballAction> getActionsByDifficulty(String difficulty) {
        return actionMapper.selectByDifficulty(difficulty);
    }

    @Override
    public BasketballAction getActionById(Long actionId) {
        BasketballAction action = actionMapper.selectById(actionId);
        if (action == null) {
            throw new RuntimeException("动作不存在");
        }
        return action;
    }

    @Override
    @Transactional
    public BasketballAction createAction(BasketballAction action) {
        actionMapper.insert(action);
        log.info("篮球动作创建成功: {}", action.getName());
        return action;
    }

    @Override
    @Transactional
    public BasketballAction updateAction(Long actionId, BasketballAction action) {
        BasketballAction existing = actionMapper.selectById(actionId);
        if (existing == null) {
            throw new RuntimeException("动作不存在");
        }

        existing.setName(action.getName());
        existing.setCategory(action.getCategory());
        existing.setDifficulty(action.getDifficulty());
        existing.setVideoUrl(action.getVideoUrl());
        existing.setImageUrl(action.getImageUrl());
        existing.setTrainingPoints(action.getTrainingPoints());
        existing.setTips(action.getTips());
        existing.setIsPublished(action.getIsPublished());

        actionMapper.updateById(existing);
        log.info("篮球动作更新成功: {}", actionId);
        return existing;
    }

    @Override
    @Transactional
    public void deleteAction(Long actionId) {
        BasketballAction action = actionMapper.selectById(actionId);
        if (action == null) {
            throw new RuntimeException("动作不存在");
        }
        actionMapper.deleteById(actionId);
        log.info("篮球动作删除成功: {}", actionId);
    }

}
