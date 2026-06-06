package com.example.basketball.service;

import com.example.basketball.entity.GameMatch;

/**
 * 比赛服务接口
 */
public interface GameMatchService {

    /**
     * 获取比赛信息
     */
    GameMatch getMatchById(Long id);

    /**
     * 创建模拟比赛
     */
    GameMatch createSimulatedMatch();

    /**
     * 更新比赛数据（模拟实时更新）
     */
    GameMatch updateMatchData(Long id);

    /**
     * 开始/暂停比赛
     */
    GameMatch toggleMatch(Long id);

    /**
     * 重置比赛
     */
    GameMatch resetMatch(Long id);
}