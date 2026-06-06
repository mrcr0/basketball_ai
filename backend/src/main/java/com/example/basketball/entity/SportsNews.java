package com.example.basketball.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sports_news")
public class SportsNews {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("summary")
    private String summary;

    @TableField("content")
    private String content;

    @TableField("news_type")
    private String newsType;

    @TableField("league")
    private String league;

    @TableField("source")
    private String source;

    @TableField("source_url")
    private String sourceUrl;

    @TableField("tags")
    private String tags;

    @TableField("match_id")
    private Long matchId;

    @TableField("team_names")
    private String teamNames;

    @TableField("player_names")
    private String playerNames;

    @TableField("cover_image")
    private String coverImage;

    @TableField("is_published")
    private Integer isPublished;

    @TableField("is_verified")
    private Integer isVerified;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("publish_time")
    private LocalDateTime publishTime;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getNewsType() { return newsType; }
    public void setNewsType(String newsType) { this.newsType = newsType; }
    public String getLeague() { return league; }
    public void setLeague(String league) { this.league = league; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }
    public String getTeamNames() { return teamNames; }
    public void setTeamNames(String teamNames) { this.teamNames = teamNames; }
    public String getPlayerNames() { return playerNames; }
    public void setPlayerNames(String playerNames) { this.playerNames = playerNames; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Integer getIsPublished() { return isPublished; }
    public void setIsPublished(Integer isPublished) { this.isPublished = isPublished; }
    public Integer getIsVerified() { return isVerified; }
    public void setIsVerified(Integer isVerified) { this.isVerified = isVerified; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public LocalDateTime getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
