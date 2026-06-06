package com.example.basketball.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.basketball.config.TencentSportsProperties;
import com.example.basketball.dto.tencent.TencentApiResponse;
import com.example.basketball.dto.tencent.TencentMatchItem;
import com.example.basketball.dto.tencent.TencentNewsItem;
import com.example.basketball.entity.SportsNews;
import com.example.basketball.service.ApiCacheManager;
import com.example.basketball.service.RateLimiter;

/**
 * 腾讯体育API客户端
 * <p>
 * 负责与腾讯体育开放平台进行安全的HTTP通信，包括：
 * 1. HMAC-SHA256 签名认证
 * 2. API请求发送与响应解析
 * 3. 缓存管理（避免重复请求）
 * 4. 限流控制（遵守频率限制）
 * 5. 错误处理与降级策略
 * 6. API数据 → SportsNews实体映射
 * <p>
 * 使用方式：注入此组件后调用 fetchXxx() 方法获取数据。
 * 当 enabled=false 或 API 不可用时，自动降级到模拟数据。
 */
@Component
public class TencentSportsApiClient {

    private static final Logger log = LoggerFactory.getLogger(TencentSportsApiClient.class);

    private final TencentSportsProperties props;
    private final ApiCacheManager cacheManager;
    private final RateLimiter rateLimiter;
    private final HttpClient httpClient;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /** 连续失败计数器（用于熔断） */
    private volatile int consecutiveFailures = 0;
    private static final int CIRCUIT_BREAKER_THRESHOLD = 5;
    /** 熔断恢复时间（毫秒） */
    private volatile long circuitOpenUntil = 0;

    public TencentSportsApiClient(TencentSportsProperties props, ApiCacheManager cacheManager, RateLimiter rateLimiter) {
        this.props = props;
        this.cacheManager = cacheManager;
        this.rateLimiter = rateLimiter;
        this.rateLimiter.init(props.getMaxRequestsPerMinute());
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(props.getConnectTimeout()))
                .build();
        log.info("TencentSportsApiClient initialized. enabled={}, baseUrl={}", props.isEnabled(), props.getBaseUrl());
    }

    // ========== 公开API方法 ==========

    /**
     * 获取比赛数据（赛程、比分、统计）
     */
    public List<SportsNews> fetchMatchData() {
        return fetchWithFallback(
            "match_data",
            () -> doFetchMatchData(),
            "比赛数据"
        );
    }

    /**
     * 获取球队动态
     */
    public List<SportsNews> fetchTeamDynamics() {
        return fetchWithFallback(
            "team_dynamics",
            () -> doFetchTeamDynamics(),
            "球队动态"
        );
    }

    /**
     * 获取球员表现数据
     */
    public List<SportsNews> fetchPlayerPerformances() {
        return fetchWithFallback(
            "player_performance",
            () -> doFetchPlayerPerformances(),
            "球员表现"
        );
    }

    /**
     * 获取新闻资讯
     */
    public List<SportsNews> fetchNews() {
        return fetchWithFallback(
            "news",
            () -> doFetchNews(),
            "综合资讯"
        );
    }

    /**
     * 获取所有类型数据（合并）
     */
    public List<SportsNews> fetchAll() {
        List<SportsNews> all = new ArrayList<>();
        all.addAll(fetchMatchData());
        all.addAll(fetchTeamDynamics());
        all.addAll(fetchPlayerPerformances());
        all.addAll(fetchNews());
        return all;
    }

    /**
     * API健康检查
     */
    public boolean isHealthy() {
        if (!props.isEnabled()) return true;
        if (circuitOpenUntil > System.currentTimeMillis()) return false;
        return consecutiveFailures < CIRCUIT_BREAKER_THRESHOLD;
    }

    /**
     * 获取统计数据
     */
    public String getStats() {
        return String.format(
            "TencentSportsApi[enabled=%s, healthy=%s, failures=%d, cacheSize=%d, rateLimit=%.1f%%]",
            props.isEnabled(), isHealthy(), consecutiveFailures, cacheManager.size(), rateLimiter.getUsageRate()
        );
    }

    // ========== 内部API调用 ==========

    private List<SportsNews> fetchWithFallback(String cacheKey, ApiCaller caller, String defaultNewsType) {
        // 1. 检查缓存
        @SuppressWarnings("unchecked")
        List<SportsNews> cached = cacheManager.get("tencent_" + cacheKey, (Class<List<SportsNews>>) (Class<?>) List.class);
        if (cached != null) {
            return cached;
        }

        // 2. 如果API未启用或熔断中，直接返回空（由上层Service兜底mock）
        if (!props.isEnabled()) {
            return Collections.emptyList();
        }
        if (circuitOpenUntil > System.currentTimeMillis()) {
            log.warn("Circuit breaker open, skipping API call for {}", cacheKey);
            return Collections.emptyList();
        }

        // 3. 限流检查
        if (!rateLimiter.tryAcquire()) {
            log.warn("Rate limited, skipping API call for {}", cacheKey);
            return Collections.emptyList();
        }

        // 4. 调用API
        try {
            List<SportsNews> result = caller.call();
            consecutiveFailures = 0;
            // 缓存结果
            long ttlMs = props.getCacheTtlSeconds() * 1000L;
            cacheManager.put("tencent_" + cacheKey, result, ttlMs);
            log.info("Tencent API fetch success: {} returned {} items", cacheKey, result.size());
            return result;
        } catch (Exception e) {
            consecutiveFailures++;
            log.error("Tencent API call failed for {} (failure #{}/{}): {}",
                    cacheKey, consecutiveFailures, CIRCUIT_BREAKER_THRESHOLD, e.getMessage());
            if (consecutiveFailures >= CIRCUIT_BREAKER_THRESHOLD) {
                circuitOpenUntil = System.currentTimeMillis() + 300_000; // 5分钟熔断
                log.warn("Circuit breaker OPEN for 5 minutes due to {} consecutive failures", consecutiveFailures);
            }
            return Collections.emptyList();
        }
    }

    private List<SportsNews> doFetchMatchData() {
        String url = props.getBaseUrl() + props.getMatchPath() + "?league_id=" + props.getNbaLeagueId();
        TencentApiResponse<List<TencentMatchItem>> response = executeGet(url,
                new TypeReference<TencentApiResponse<List<TencentMatchItem>>>() {});

        if (!response.isSuccess() || response.getData() == null) {
            log.warn("Match data API returned non-success: code={}", response.getCode());
            return Collections.emptyList();
        }

        List<SportsNews> result = new ArrayList<>();
        for (TencentMatchItem match : response.getData()) {
            result.add(mapMatchToNews(match));
        }
        return result;
    }

    private List<SportsNews> doFetchTeamDynamics() {
        String url = props.getBaseUrl() + props.getTeamPath() + "?league_id=" + props.getNbaLeagueId();
        TencentApiResponse<List<TencentNewsItem>> response = executeGet(url,
                new TypeReference<TencentApiResponse<List<TencentNewsItem>>>() {});

        if (!response.isSuccess() || response.getData() == null) {
            return Collections.emptyList();
        }

        List<SportsNews> result = new ArrayList<>();
        for (TencentNewsItem item : response.getData()) {
            result.add(mapNewsItemToSportsNews(item));
        }
        return result;
    }

    private List<SportsNews> doFetchPlayerPerformances() {
        String url = props.getBaseUrl() + props.getPlayerPath() + "?league_id=" + props.getNbaLeagueId();
        TencentApiResponse<List<TencentNewsItem>> response = executeGet(url,
                new TypeReference<TencentApiResponse<List<TencentNewsItem>>>() {});

        if (!response.isSuccess() || response.getData() == null) {
            return Collections.emptyList();
        }

        List<SportsNews> result = new ArrayList<>();
        for (TencentNewsItem item : response.getData()) {
            result.add(mapNewsItemToSportsNews(item));
        }
        return result;
    }

    private List<SportsNews> doFetchNews() {
        String url = props.getBaseUrl() + props.getNewsPath() + "?league_id=" + props.getNbaLeagueId();
        TencentApiResponse<List<TencentNewsItem>> response = executeGet(url,
                new TypeReference<TencentApiResponse<List<TencentNewsItem>>>() {});

        if (!response.isSuccess() || response.getData() == null) {
            return Collections.emptyList();
        }

        List<SportsNews> result = new ArrayList<>();
        for (TencentNewsItem item : response.getData()) {
            result.add(mapNewsItemToSportsNews(item));
        }
        return result;
    }

    // ========== HTTP 请求 ==========

    private <T> T executeGet(String url, TypeReference<T> typeRef) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonce = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            String signature = generateSignature(timestamp, nonce);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("X-Api-Key", props.getApiKey())
                    .header("X-Timestamp", timestamp)
                    .header("X-Nonce", nonce)
                    .header("X-Signature", signature)
                    .header("User-Agent", "BasketballTraining/1.0")
                    .timeout(Duration.ofMillis(props.getReadTimeout()))
                    .GET()
                    .build();

            log.debug(">> GET {}", url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != 200) {
                log.error("Tencent API returned HTTP {}: {}", response.statusCode(), response.body());
                throw new RuntimeException("API HTTP error: " + response.statusCode());
            }

            String body = response.body();
            log.debug("<< Response length: {} chars", body != null ? body.length() : 0);

            return JSON.parseObject(body, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("Tencent API request failed: " + url, e);
        }
    }

    // ========== 签名认证 ==========

    /**
     * 生成HMAC-SHA256请求签名
     * 签名算法：HMAC-SHA256(apiSecret, apiKey + timestamp + nonce)
     */
    private String generateSignature(String timestamp, String nonce) {
        if (props.getApiSecret() == null || props.getApiSecret().isEmpty()) {
            return "";
        }
        try {
            String data = props.getApiKey() + timestamp + nonce;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec spec = new SecretKeySpec(props.getApiSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(spec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            log.error("Failed to generate API signature", e);
            return "";
        }
    }

    // ========== 数据映射 ==========

    /**
     * 将比赛数据映射为 SportsNews 实体
     */
    private SportsNews mapMatchToNews(TencentMatchItem match) {
        SportsNews news = new SportsNews();
        String title = match.getHomeTeam() + " vs " + match.getAwayTeam();
        if (match.getHomeScore() != null && match.getAwayScore() != null) {
            title += " " + match.getHomeScore() + "-" + match.getAwayScore();
        }
        news.setTitle(title);

        StringBuilder content = new StringBuilder();
        content.append("【比赛信息】\n");
        content.append("联赛：").append(match.getLeague()).append("\n");
        content.append("状态：").append(getStatusText(match.getStatus())).append("\n");
        if (match.getPeriod() != null) {
            content.append("当前节次：").append(match.getPeriod()).append("\n");
        }
        if (match.getHomeScore() != null) {
            content.append("比分：").append(match.getHomeTeam()).append(" ")
                  .append(match.getHomeScore()).append(" - ").append(match.getAwayScore())
                  .append(" ").append(match.getAwayTeam()).append("\n");
        }

        // 球员统计数据
        if (match.getTopPlayers() != null && !match.getTopPlayers().isEmpty()) {
            content.append("\n【关键球员】\n");
            for (TencentMatchItem.PlayerStat p : match.getTopPlayers()) {
                content.append("· ").append(p.getName()).append("：")
                      .append(p.getPoints()).append("分 ")
                      .append(p.getRebounds()).append("篮板 ")
                      .append(p.getAssists()).append("助攻\n");
            }
        }

        // 球队统计数据
        if (match.getHomeStats() != null && match.getAwayStats() != null) {
            content.append("\n【技术统计】\n");
            content.append(formatTeamStatsLine("投篮", match.getHomeStats().getFieldGoalsMade(),
                    match.getHomeStats().getFieldGoalsAttempted(),
                    match.getAwayStats().getFieldGoalsMade(),
                    match.getAwayStats().getFieldGoalsAttempted()));
            content.append(formatTeamStatsLine("三分", match.getHomeStats().getThreePointersMade(),
                    match.getHomeStats().getThreePointersAttempted(),
                    match.getAwayStats().getThreePointersMade(),
                    match.getAwayStats().getThreePointersAttempted()));
            content.append(formatTeamStatsLine("罚球", match.getHomeStats().getFreeThrowsMade(),
                    match.getHomeStats().getFreeThrowsAttempted(),
                    match.getAwayStats().getFreeThrowsMade(),
                    match.getAwayStats().getFreeThrowsAttempted()));
            content.append("· 篮板：").append(match.getHomeStats().getRebounds())
                    .append(" vs ").append(match.getAwayStats().getRebounds()).append("\n");
            content.append("· 助攻：").append(match.getHomeStats().getAssists())
                    .append(" vs ").append(match.getAwayStats().getAssists()).append("\n");
              content.append("· 失误：").append(match.getHomeStats().getTurnovers())
                      .append(" vs ").append(match.getAwayStats().getTurnovers()).append("\n");
        }

        news.setContent(content.toString());
        news.setSummary(match.getHomeTeam() + " " + (match.getHomeScore() != null ? match.getHomeScore() : "") + "-" +
                        (match.getAwayScore() != null ? match.getAwayScore() : "") + " " + match.getAwayTeam());
        news.setNewsType("比赛数据");
        news.setLeague(match.getLeague());
        news.setSource("腾讯体育");
        news.setTags(match.getLeague() + ",比赛,实时数据");
        news.setTeamNames(match.getHomeTeam() + "," + match.getAwayTeam());

        if (match.getTopPlayers() != null) {
            StringBuilder players = new StringBuilder();
            for (TencentMatchItem.PlayerStat p : match.getTopPlayers()) {
                if (players.length() > 0) players.append(",");
                players.append(p.getName());
            }
            news.setPlayerNames(players.toString());
        }

        try {
            news.setPublishTime(LocalDateTime.parse(match.getStartTime(), ISO_FORMATTER));
        } catch (Exception e) {
            news.setPublishTime(LocalDateTime.now());
        }

        return news;
    }

    /**
     * 将新闻资讯DTO映射为 SportsNews 实体
     */
    private SportsNews mapNewsItemToSportsNews(TencentNewsItem item) {
        SportsNews news = new SportsNews();
        news.setTitle(item.getTitle());
        news.setSummary(item.getSummary());
        news.setContent(item.getContent());
        news.setNewsType(mapCategory(item.getCategory()));
        news.setLeague(item.getLeague());
        news.setSource("腾讯体育");
        news.setSourceUrl(item.getSourceUrl());
        news.setTags(item.getTags());
        news.setTeamNames(item.getTeamNames());
        news.setPlayerNames(item.getPlayerNames());
        news.setCoverImage(item.getCoverImage());
        news.setViewCount(item.getViewCount() != null ? item.getViewCount() : 0);

        try {
            news.setPublishTime(LocalDateTime.parse(item.getPublishTime(), ISO_FORMATTER));
        } catch (Exception e) {
            news.setPublishTime(LocalDateTime.now());
        }

        return news;
    }

    private String mapCategory(String apiCategory) {
        if (apiCategory == null) return "综合资讯";
        switch (apiCategory) {
            case "match_data": return "比赛数据";
            case "team_dynamic": return "球队动态";
            case "player_performance": return "球员表现";
            case "tactic_analysis": return "战术分析";
            case "transfer": return "转会新闻";
            case "gossip": return "花边新闻";
            default: return "综合资讯";
        }
    }

    private String getStatusText(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "live": return "进行中";
            case "finished": return "已结束";
            case "upcoming": return "即将开始";
            default: return status;
        }
    }

    private String formatTeamStatsLine(String label, Integer hm, Integer ha, Integer am, Integer aa) {
        if (hm == null || ha == null || am == null || aa == null) return "";
        return "· " + label + "：" + hm + "/" + ha + " vs " + am + "/" + aa + "\n";
    }

    // ========== 内部接口 ==========

    @FunctionalInterface
    private interface ApiCaller {
        List<SportsNews> call() throws Exception;
    }
}
