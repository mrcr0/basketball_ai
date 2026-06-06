package com.example.basketball.impl;

import com.example.basketball.entity.GameMatch;
import com.example.basketball.mapper.GameMatchMapper;
import com.example.basketball.service.GameMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 比赛服务实现
 */
@Service
public class GameMatchServiceImpl implements GameMatchService {

    @Autowired
    private GameMatchMapper gameMatchMapper;

    private final Random random = new Random();

    /**
     * 球队名称列表
     */
    private static final String[] TEAMS = {
            "洛杉矶湖人", "金州勇士", "波士顿凯尔特人", "迈阿密热火",
            "芝加哥公牛", "休斯顿火箭", "达拉斯独行侠", "菲尼克斯太阳",
            "丹佛掘金", "密尔沃基雄鹿", "费城76人", "布鲁克林篮网"
    };

    @Override
    public GameMatch getMatchById(Long id) {
        return gameMatchMapper.selectById(id);
    }

    @Override
    public GameMatch createSimulatedMatch() {
        GameMatch match = new GameMatch();

        // 随机选择两支球队
        int homeIndex = random.nextInt(TEAMS.length);
        int awayIndex;
        do {
            awayIndex = random.nextInt(TEAMS.length);
        } while (awayIndex == homeIndex);

        match.setMatchName(TEAMS[homeIndex] + " vs " + TEAMS[awayIndex]);
        match.setHomeTeam(TEAMS[homeIndex]);
        match.setAwayTeam(TEAMS[awayIndex]);
        match.setHomeScore(0);
        match.setAwayScore(0);
        match.setStatus("未开始");
        match.setCurrentQuarter(1);
        match.setRemainingTime(720); // 12分钟 = 720秒
        match.setHomePossession(true);
        match.setCreatedAt(LocalDateTime.now());
        match.setUpdatedAt(LocalDateTime.now());

        gameMatchMapper.insert(match);
        return match;
    }

    @Override
    public GameMatch updateMatchData(Long id) {
        GameMatch match = gameMatchMapper.selectById(id);
        if (match == null) {
            return null;
        }

        if (!"进行中".equals(match.getStatus())) {
            return match;
        }

        // 减少剩余时间
        match.setRemainingTime(match.getRemainingTime() - 1);

        // 模拟比赛数据更新
        if (random.nextDouble() < 0.15) { // 15%概率得分
            int points = random.nextBoolean() ? 2 : 3; // 2分或3分
            if (match.getHomePossession()) {
                match.setHomeScore(match.getHomeScore() + points);
            } else {
                match.setAwayScore(match.getAwayScore() + points);
            }
            // 交换控球权
            match.setHomePossession(!match.getHomePossession());
        } else if (random.nextDouble() < 0.05) { // 5%概率失误/犯规
            match.setHomePossession(!match.getHomePossession());
        }

        // 检查节次结束
        if (match.getRemainingTime() <= 0) {
            if (match.getCurrentQuarter() < 4) {
                match.setCurrentQuarter(match.getCurrentQuarter() + 1);
                match.setRemainingTime(720); // 下一节开始
            } else {
                match.setStatus("已结束");
                match.setRemainingTime(0);
                match.setEndTime(LocalDateTime.now());
            }
        }

        match.setUpdatedAt(LocalDateTime.now());
        gameMatchMapper.updateById(match);
        return match;
    }

    @Override
    public GameMatch toggleMatch(Long id) {
        GameMatch match = gameMatchMapper.selectById(id);
        if (match == null) {
            return null;
        }

        if ("未开始".equals(match.getStatus()) || "暂停".equals(match.getStatus())) {
            match.setStatus("进行中");
            if (match.getStartTime() == null) {
                match.setStartTime(LocalDateTime.now());
            }
        } else if ("进行中".equals(match.getStatus())) {
            match.setStatus("暂停");
        }

        match.setUpdatedAt(LocalDateTime.now());
        gameMatchMapper.updateById(match);
        return match;
    }

    @Override
    public GameMatch resetMatch(Long id) {
        GameMatch match = gameMatchMapper.selectById(id);
        if (match == null) {
            return null;
        }

        match.setHomeScore(0);
        match.setAwayScore(0);
        match.setStatus("未开始");
        match.setCurrentQuarter(1);
        match.setRemainingTime(720);
        match.setHomePossession(true);
        match.setStartTime(null);
        match.setEndTime(null);
        match.setUpdatedAt(LocalDateTime.now());

        gameMatchMapper.updateById(match);
        return match;
    }
}