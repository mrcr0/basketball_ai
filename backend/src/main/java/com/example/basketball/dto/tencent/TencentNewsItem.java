package com.example.basketball.dto.tencent;

/**
 * 腾讯体育-新闻资讯DTO
 */
public class TencentNewsItem {

    /** 新闻ID */
    private String newsId;

    /** 标题 */
    private String title;

    /** 摘要 */
    private String summary;

    /** 正文内容 */
    private String content;

    /** 分类：match_data/team_dynamic/player_performance/tactic_analysis/transfer/gossip */
    private String category;

    /** 联赛 */
    private String league;

    /** 来源 */
    private String source;

    /** 原始链接 */
    private String sourceUrl;

    /** 标签（逗号分隔） */
    private String tags;

    /** 涉及球队 */
    private String teamNames;

    /** 涉及球员 */
    private String playerNames;

    /** 发布时间（ISO8601格式） */
    private String publishTime;

    /** 浏览量 */
    private Integer viewCount;

    /** 封面图URL */
    private String coverImage;

    // ===== Getters & Setters =====

    public String getNewsId() { return newsId; }
    public void setNewsId(String newsId) { this.newsId = newsId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLeague() { return league; }
    public void setLeague(String league) { this.league = league; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getTeamNames() { return teamNames; }
    public void setTeamNames(String teamNames) { this.teamNames = teamNames; }

    public String getPlayerNames() { return playerNames; }
    public void setPlayerNames(String playerNames) { this.playerNames = playerNames; }

    public String getPublishTime() { return publishTime; }
    public void setPublishTime(String publishTime) { this.publishTime = publishTime; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
}
