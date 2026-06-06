package com.example.basketball.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * API 响应缓存管理器
 * 基于内存的 TTL 缓存，避免短时间内重复请求腾讯体育API
 */
@Component
public class ApiCacheManager {

    private static final Logger log = LoggerFactory.getLogger(ApiCacheManager.class);

    private final ConcurrentMap<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();

    /**
     * 从缓存获取数据
     * @param key   缓存键
     * @param type  数据类型Class
     * @return 缓存的数据，如果不存在或已过期则返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        CacheEntry<?> entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            cache.remove(key);
            log.debug("Cache expired for key: {}", key);
            return null;
        }
        log.debug("Cache hit for key: {}", key);
        return (T) entry.data;
    }

    /**
     * 将数据存入缓存
     * @param key      缓存键
     * @param data     数据
     * @param ttlMs    TTL（毫秒）
     */
    public <T> void put(String key, T data, long ttlMs) {
        cache.put(key, new CacheEntry<>(data, ttlMs));
        log.debug("Cache put for key: {}, TTL: {}ms", key, ttlMs);
    }

    /**
     * 清除指定缓存
     */
    public void invalidate(String key) {
        cache.remove(key);
        log.debug("Cache invalidated for key: {}", key);
    }

    /**
     * 清除所有缓存
     */
    public void invalidateAll() {
        cache.clear();
        log.info("All API caches invalidated");
    }

    /**
     * 获取缓存大小
     */
    public int size() {
        return cache.size();
    }

    /**
     * 清理过期缓存
     */
    public void cleanExpired() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private static class CacheEntry<T> {
        final T data;
        final long expireAt;

        CacheEntry(T data, long ttlMs) {
            this.data = data;
            this.expireAt = System.currentTimeMillis() + ttlMs;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireAt;
        }
    }
}
