package com.example.basketball.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.basketball.ai.DeepSeekClient;
import com.example.basketball.entity.TacticInfo;
import com.example.basketball.mapper.TacticInfoMapper;
import com.example.basketball.service.TacticInfoService;

@Service
public class TacticInfoServiceImpl implements TacticInfoService {

    private static final Logger log = LoggerFactory.getLogger(TacticInfoServiceImpl.class);

    private final TacticInfoMapper tacticMapper;
    private final DeepSeekClient deepSeekClient;

    public TacticInfoServiceImpl(TacticInfoMapper tacticMapper, DeepSeekClient deepSeekClient) {
        this.tacticMapper = tacticMapper;
        this.deepSeekClient = deepSeekClient;
    }

    @Override
    public List<TacticInfo> getAllTactics() {
        return tacticMapper.selectList(null);
    }

    @Override
    public List<TacticInfo> getTacticsByType(String type) {
        return tacticMapper.selectByType(type);
    }

    @Override
    public TacticInfo getTacticById(Long tacticId) {
        TacticInfo tactic = tacticMapper.selectById(tacticId);
        if (tactic == null) {
            throw new RuntimeException("战术资讯不存在");
        }

        tactic.setViewCount(tactic.getViewCount() + 1);
        tacticMapper.updateById(tactic);
        return tactic;
    }

    @Override
    @Transactional
    public TacticInfo createTactic(TacticInfo tactic) {
        tacticMapper.insert(tactic);
        log.info("战术资讯创建成功: {}", tactic.getTitle());
        return tactic;
    }

    @Override
    @Transactional
    public TacticInfo updateTactic(Long tacticId, TacticInfo tactic) {
        TacticInfo existing = tacticMapper.selectById(tacticId);
        if (existing == null) {
            throw new RuntimeException("战术资讯不存在");
        }

        existing.setTitle(tactic.getTitle());
        existing.setContent(tactic.getContent());
        existing.setType(tactic.getType());
        existing.setCoverImage(tactic.getCoverImage());
        existing.setIsPublished(tactic.getIsPublished());

        tacticMapper.updateById(existing);
        log.info("战术资讯更新成功: {}", tacticId);
        return existing;
    }

    @Override
    @Transactional
    public void deleteTactic(Long tacticId) {
        TacticInfo tactic = tacticMapper.selectById(tacticId);
        if (tactic == null) {
            throw new RuntimeException("战术资讯不存在");
        }
        tacticMapper.deleteById(tacticId);
        log.info("战术资讯删除成功: {}", tacticId);
    }

    @Override
    public String explainTacticByName(String tacticName) {
        return deepSeekClient.explainTactic(tacticName);
    }

}
