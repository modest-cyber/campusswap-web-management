package com.modestcyber.service;

import com.modestcyber.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务
 */
@Slf4j
@Service
public class CacheService {

    @Autowired
    private RedisUtil redisUtil;

    private static final String CACHE_PREFIX = "campusswap:";

    /**
     * 缓存商品浏览量
     */
    public void cacheProductViewCount(Long productId, Long viewCount) {
        String key = CACHE_PREFIX + "product:view:" + productId;
        redisUtil.set(key, viewCount, 24, TimeUnit.HOURS);
    }

    /**
     * 获取缓存的商品浏览量
     */
    public Long getCachedProductViewCount(Long productId) {
        String key = CACHE_PREFIX + "product:view:" + productId;
        Object value = redisUtil.get(key);
        return value != null ? Long.valueOf(value.toString()) : null;
    }

    /**
     * 增加商品浏览量
     */
    public Long incrementProductViewCount(Long productId) {
        String key = CACHE_PREFIX + "product:view:" + productId;
        return redisUtil.increment(key);
    }

    /**
     * 缓存热门商品列表
     */
    public void cacheHotProducts(Object hotProducts) {
        String key = CACHE_PREFIX + "hot:products";
        redisUtil.set(key, hotProducts, 1, TimeUnit.HOURS);
    }

    /**
     * 获取缓存的热门商品列表
     */
    public Object getCachedHotProducts() {
        String key = CACHE_PREFIX + "hot:products";
        return redisUtil.get(key);
    }

    /**
     * 删除缓存
     */
    public void deleteCache(String cacheKey) {
        String key = CACHE_PREFIX + cacheKey;
        redisUtil.delete(key);
    }

    /**
     * 缓存用户Token
     */
    public void cacheUserToken(Long userId, String token) {
        String key = CACHE_PREFIX + "user:token:" + userId;
        redisUtil.set(key, token, 24, TimeUnit.HOURS);
    }

    /**
     * 获取用户Token
     */
    public String getUserToken(Long userId) {
        String key = CACHE_PREFIX + "user:token:" + userId;
        Object value = redisUtil.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 删除用户Token（登出）
     */
    public void deleteUserToken(Long userId) {
        String key = CACHE_PREFIX + "user:token:" + userId;
        redisUtil.delete(key);
    }

    /**
     * 检查缓存是否存在
     */
    public boolean hasCache(String cacheKey) {
        String key = CACHE_PREFIX + cacheKey;
        return Boolean.TRUE.equals(redisUtil.hasKey(key));
    }
}
