package com.example.basketball.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯体育API配置属性
 * 对应 application.yml 中的 app.tencent.sports 配置节点
 */
@Component
@ConfigurationProperties(prefix = "app.tencent.sports")
public class TencentSportsProperties {

    /** API 基础地址 */
    private String baseUrl = "https://open.sports.qq.com";

    /** API 访问密钥ID（需在腾讯体育开放平台申请） */
    private String apiKey = "";

    /** API 访问密钥Secret（用于请求签名） */
    private String apiSecret = "";

    /** 是否启用真实API调用（未获取密钥时使用模拟数据） */
    private boolean enabled = false;

    /** HTTP 连接超时（毫秒） */
    private int connectTimeout = 5000;

    /** HTTP 读取超时（毫秒） */
    private int readTimeout = 10000;

    /** 缓存过期时间（秒），默认5分钟 */
    private int cacheTtlSeconds = 300;

    /** 每分钟最大请求次数（遵守频率限制） */
    private int maxRequestsPerMinute = 50;

    /** 数据刷新间隔（毫秒），默认30分钟 */
    private long refreshIntervalMs = 1800000;

    /** 篮球赛事接口路径 */
    private String matchPath = "/api/v1/basketball/matches";

    /** 球队动态接口路径 */
    private String teamPath = "/api/v1/basketball/teams";

    /** 球员数据接口路径 */
    private String playerPath = "/api/v1/basketball/players";

    /** 新闻资讯接口路径 */
    private String newsPath = "/api/v1/basketball/news";

    /** NBA联赛ID */
    private String nbaLeagueId = "1";

    /** FIBA联赛ID */
    private String fibaLeagueId = "2";

    /** CBA联赛ID */
    private String cbaLeagueId = "3";

    // ===== Getters & Setters =====

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(int connectTimeout) { this.connectTimeout = connectTimeout; }

    public int getReadTimeout() { return readTimeout; }
    public void setReadTimeout(int readTimeout) { this.readTimeout = readTimeout; }

    public int getCacheTtlSeconds() { return cacheTtlSeconds; }
    public void setCacheTtlSeconds(int cacheTtlSeconds) { this.cacheTtlSeconds = cacheTtlSeconds; }

    public int getMaxRequestsPerMinute() { return maxRequestsPerMinute; }
    public void setMaxRequestsPerMinute(int maxRequestsPerMinute) { this.maxRequestsPerMinute = maxRequestsPerMinute; }

    public long getRefreshIntervalMs() { return refreshIntervalMs; }
    public void setRefreshIntervalMs(long refreshIntervalMs) { this.refreshIntervalMs = refreshIntervalMs; }

    public String getMatchPath() { return matchPath; }
    public void setMatchPath(String matchPath) { this.matchPath = matchPath; }

    public String getTeamPath() { return teamPath; }
    public void setTeamPath(String teamPath) { this.teamPath = teamPath; }

    public String getPlayerPath() { return playerPath; }
    public void setPlayerPath(String playerPath) { this.playerPath = playerPath; }

    public String getNewsPath() { return newsPath; }
    public void setNewsPath(String newsPath) { this.newsPath = newsPath; }

    public String getNbaLeagueId() { return nbaLeagueId; }
    public void setNbaLeagueId(String nbaLeagueId) { this.nbaLeagueId = nbaLeagueId; }

    public String getFibaLeagueId() { return fibaLeagueId; }
    public void setFibaLeagueId(String fibaLeagueId) { this.fibaLeagueId = fibaLeagueId; }

    public String getCbaLeagueId() { return cbaLeagueId; }
    public void setCbaLeagueId(String cbaLeagueId) { this.cbaLeagueId = cbaLeagueId; }
}
