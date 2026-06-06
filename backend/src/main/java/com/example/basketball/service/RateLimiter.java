package com.example.basketball.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 令牌桶限流器
 * 用于控制对腾讯体育API的请求频率，遵守API使用规范
 */
@Component
public class RateLimiter {

    private static final Logger log = LoggerFactory.getLogger(RateLimiter.class);

    /** 最大令牌数 */
    private volatile long maxTokens;

    /** 当前可用令牌数 */
    private volatile long availableTokens;

    /** 上次补充令牌的时间戳（毫秒） */
    private volatile long lastRefillTime;

    /** 令牌补充速率（每毫秒补充的令牌数） */
    private volatile double refillRatePerMs;

    /** 锁对象 */
    private final Object lock = new Object();

    public RateLimiter() {
        this.maxTokens = 50;
        this.availableTokens = 50;
        this.lastRefillTime = System.currentTimeMillis();
        this.refillRatePerMs = 50.0 / 60000.0;
    }

    /**
     * 初始化限流器
     * @param maxRequestsPerMinute 每分钟最大请求数
     */
    public synchronized void init(int maxRequestsPerMinute) {
        this.maxTokens = maxRequestsPerMinute;
        this.availableTokens = maxRequestsPerMinute;
        this.lastRefillTime = System.currentTimeMillis();
        this.refillRatePerMs = (double) maxRequestsPerMinute / 60000.0;
        log.info("Rate limiter initialized: {} requests/min", maxRequestsPerMinute);
    }

    /**
     * 尝试获取一个令牌
     * @return true-获取成功，false-被限流
     */
    public boolean tryAcquire() {
        synchronized (lock) {
            refillTokens();
            if (availableTokens > 0) {
                availableTokens--;
                return true;
            }
            log.warn("Rate limit exceeded. Available: 0/{}", maxTokens);
            return false;
        }
    }

    /**
     * 补充令牌（根据时间流逝计算）
     */
    private void refillTokens() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        if (elapsed > 0) {
            long newTokens = (long) (elapsed * refillRatePerMs);
            if (newTokens > 0) {
                availableTokens = Math.min(maxTokens, availableTokens + newTokens);
                lastRefillTime = now;
            }
        }
    }

    /**
     * 获取当前可用令牌数
     */
    public long getAvailableTokens() {
        synchronized (lock) {
            refillTokens();
            return availableTokens;
        }
    }

    /**
     * 获取当前使用率（百分比）
     */
    public double getUsageRate() {
        synchronized (lock) {
            refillTokens();
            if (maxTokens == 0) return 0;
            return (1.0 - (double) availableTokens / maxTokens) * 100;
        }
    }
}
