package com.example.basketball.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.basketball.entity.BasketballKnowledge;
import com.example.basketball.mapper.BasketballKnowledgeMapper;

@Service
public class BasketballKnowledgeService {

    private final BasketballKnowledgeMapper knowledgeMapper;

    public BasketballKnowledgeService(BasketballKnowledgeMapper knowledgeMapper) {
        this.knowledgeMapper = knowledgeMapper;
    }

    public List<BasketballKnowledge> getAllKnowledge() {
        return knowledgeMapper.selectList(
            new QueryWrapper<BasketballKnowledge>()
                .eq("is_published", 1)
                .orderByDesc("created_at")
        );
    }

    public List<BasketballKnowledge> getKnowledgeByCategory(String category) {
        return knowledgeMapper.selectByCategory(category);
    }

    public BasketballKnowledge getKnowledgeById(Long id) {
        BasketballKnowledge knowledge = knowledgeMapper.selectById(id);
        if (knowledge != null) {
            knowledge.setViewCount(knowledge.getViewCount() == null ? 1 : knowledge.getViewCount() + 1);
            knowledgeMapper.updateById(knowledge);
        }
        return knowledge;
    }

    public List<BasketballKnowledge> getHotKnowledge(int limit) {
        return knowledgeMapper.selectHotList(limit);
    }

    public List<String> getSubCategories(String category) {
        return knowledgeMapper.selectSubCategories(category);
    }

    public BasketballKnowledge createKnowledge(BasketballKnowledge knowledge) {
        knowledgeMapper.insert(knowledge);
        return knowledge;
    }

    public BasketballKnowledge updateKnowledge(Long id, BasketballKnowledge knowledge) {
        knowledge.setId(id);
        knowledgeMapper.updateById(knowledge);
        return knowledgeMapper.selectById(id);
    }

    public void deleteKnowledge(Long id) {
        knowledgeMapper.deleteById(id);
    }
}
