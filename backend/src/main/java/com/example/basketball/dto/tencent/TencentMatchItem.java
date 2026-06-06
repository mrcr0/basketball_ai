package com.example.basketball.dto.tencent;

import java.util.List;

/**
 * 腾讯体育-比赛数据DTO
 */
public class TencentMatchItem {

    /** 比赛ID */
    private String matchId;

    /** 联赛名称 */
    private String league;

    /** 主队名称 */
    private String homeTeam;

    /** 客队名称 */
    private String awayTeam;

    /** 主队得分 */
    private Integer homeScore;

    /** 客队得分 */
    private Integer awayScore;

    /** 比赛状态：live-进行中, finished-已结束, upcoming-即将开始 */
    private String status;

    /** 比赛开始时间（ISO8601格式） */
    private String startTime;

    /** 比赛节次（如 Q1, Q2, Q3, Q4, OT） */
    private String period;

    /** 节次剩余时间（秒） */
    private Integer periodTimeLeft;

    /** 关键球员表现列表 */
    private List<PlayerStat> topPlayers;

    /** 球队统计 */
    private TeamStats homeStats;
    private TeamStats awayStats;

    public static class PlayerStat {
        private String name;
        private String team;
        private Integer points;
        private Integer rebounds;
        private Integer assists;
        private Integer steals;
        private Integer blocks;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTeam() { return team; }
        public void setTeam(String team) { this.team = team; }
        public Integer getPoints() { return points; }
        public void setPoints(Integer points) { this.points = points; }
        public Integer getRebounds() { return rebounds; }
        public void setRebounds(Integer rebounds) { this.rebounds = rebounds; }
        public Integer getAssists() { return assists; }
        public void setAssists(Integer assists) { this.assists = assists; }
        public Integer getSteals() { return steals; }
        public void setSteals(Integer steals) { this.steals = steals; }
        public Integer getBlocks() { return blocks; }
        public void setBlocks(Integer blocks) { this.blocks = blocks; }
    }

    public static class TeamStats {
        private Integer fieldGoalsMade;
        private Integer fieldGoalsAttempted;
        private Integer threePointersMade;
        private Integer threePointersAttempted;
        private Integer freeThrowsMade;
        private Integer freeThrowsAttempted;
        private Integer rebounds;
        private Integer assists;
        private Integer steals;
        private Integer blocks;
        private Integer turnovers;
        private Integer fouls;

        public Integer getFieldGoalsMade() { return fieldGoalsMade; }
        public void setFieldGoalsMade(Integer fieldGoalsMade) { this.fieldGoalsMade = fieldGoalsMade; }
        public Integer getFieldGoalsAttempted() { return fieldGoalsAttempted; }
        public void setFieldGoalsAttempted(Integer fieldGoalsAttempted) { this.fieldGoalsAttempted = fieldGoalsAttempted; }
        public Integer getThreePointersMade() { return threePointersMade; }
        public void setThreePointersMade(Integer threePointersMade) { this.threePointersMade = threePointersMade; }
        public Integer getThreePointersAttempted() { return threePointersAttempted; }
        public void setThreePointersAttempted(Integer threePointersAttempted) { this.threePointersAttempted = threePointersAttempted; }
        public Integer getFreeThrowsMade() { return freeThrowsMade; }
        public void setFreeThrowsMade(Integer freeThrowsMade) { this.freeThrowsMade = freeThrowsMade; }
        public Integer getFreeThrowsAttempted() { return freeThrowsAttempted; }
        public void setFreeThrowsAttempted(Integer freeThrowsAttempted) { this.freeThrowsAttempted = freeThrowsAttempted; }
        public Integer getRebounds() { return rebounds; }
        public void setRebounds(Integer rebounds) { this.rebounds = rebounds; }
        public Integer getAssists() { return assists; }
        public void setAssists(Integer assists) { this.assists = assists; }
        public Integer getSteals() { return steals; }
        public void setSteals(Integer steals) { this.steals = steals; }
        public Integer getBlocks() { return blocks; }
        public void setBlocks(Integer blocks) { this.blocks = blocks; }
        public Integer getTurnovers() { return turnovers; }
        public void setTurnovers(Integer turnovers) { this.turnovers = turnovers; }
        public Integer getFouls() { return fouls; }
        public void setFouls(Integer fouls) { this.fouls = fouls; }
    }

    // ===== Getters & Setters =====

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    public String getLeague() { return league; }
    public void setLeague(String league) { this.league = league; }
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    public Integer getHomeScore() { return homeScore; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }
    public Integer getAwayScore() { return awayScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public Integer getPeriodTimeLeft() { return periodTimeLeft; }
    public void setPeriodTimeLeft(Integer periodTimeLeft) { this.periodTimeLeft = periodTimeLeft; }
    public List<PlayerStat> getTopPlayers() { return topPlayers; }
    public void setTopPlayers(List<PlayerStat> topPlayers) { this.topPlayers = topPlayers; }
    public TeamStats getHomeStats() { return homeStats; }
    public void setHomeStats(TeamStats homeStats) { this.homeStats = homeStats; }
    public TeamStats getAwayStats() { return awayStats; }
    public void setAwayStats(TeamStats awayStats) { this.awayStats = awayStats; }
}
