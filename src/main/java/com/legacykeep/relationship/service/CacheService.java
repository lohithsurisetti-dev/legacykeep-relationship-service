package com.legacykeep.relationship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing Redis caching operations.
 * 
 * Provides caching for user data, relationship types, and other frequently accessed data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    // Cache keys
    private static final String USER_CACHE_PREFIX = "user:";
    private static final String RELATIONSHIP_TYPE_CACHE_PREFIX = "relationship-type:";
    private static final String USER_RELATIONSHIPS_CACHE_PREFIX = "user-relationships:";
    
    // Cache TTL
    private static final Duration USER_CACHE_TTL = Duration.ofHours(1);
    private static final Duration RELATIONSHIP_TYPE_CACHE_TTL = Duration.ofHours(24);
    private static final Duration USER_RELATIONSHIPS_CACHE_TTL = Duration.ofMinutes(30);

    /**
     * Cache user data.
     * 
     * @param userId The user ID
     * @param userData The user data to cache
     */
    public void cacheUserData(Long userId, Object userData) {
        try {
            String key = USER_CACHE_PREFIX + userId;
            redisTemplate.opsForValue().set(key, userData, USER_CACHE_TTL);
            log.debug("Cached user data for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error caching user data for user ID: {}", userId, e);
        }
    }

    /**
     * Get cached user data.
     * 
     * @param userId The user ID
     * @return The cached user data or null if not found
     */
    @Cacheable(value = "users", key = "#userId")
    public Object getCachedUserData(Long userId) {
        try {
            String key = USER_CACHE_PREFIX + userId;
            Object userData = redisTemplate.opsForValue().get(key);
            log.debug("Retrieved cached user data for user ID: {}", userId);
            return userData;
        } catch (Exception e) {
            log.error("Error retrieving cached user data for user ID: {}", userId, e);
            return null;
        }
    }

    /**
     * Cache relationship type data.
     * 
     * @param relationshipTypeId The relationship type ID
     * @param relationshipTypeData The relationship type data to cache
     */
    public void cacheRelationshipTypeData(Long relationshipTypeId, Object relationshipTypeData) {
        try {
            String key = RELATIONSHIP_TYPE_CACHE_PREFIX + relationshipTypeId;
            redisTemplate.opsForValue().set(key, relationshipTypeData, RELATIONSHIP_TYPE_CACHE_TTL);
            log.debug("Cached relationship type data for ID: {}", relationshipTypeId);
        } catch (Exception e) {
            log.error("Error caching relationship type data for ID: {}", relationshipTypeId, e);
        }
    }

    /**
     * Get cached relationship type data.
     * 
     * @param relationshipTypeId The relationship type ID
     * @return The cached relationship type data or null if not found
     */
    @Cacheable(value = "relationshipTypes", key = "#relationshipTypeId")
    public Object getCachedRelationshipTypeData(Long relationshipTypeId) {
        try {
            String key = RELATIONSHIP_TYPE_CACHE_PREFIX + relationshipTypeId;
            Object relationshipTypeData = redisTemplate.opsForValue().get(key);
            log.debug("Retrieved cached relationship type data for ID: {}", relationshipTypeId);
            return relationshipTypeData;
        } catch (Exception e) {
            log.error("Error retrieving cached relationship type data for ID: {}", relationshipTypeId, e);
            return null;
        }
    }

    /**
     * Cache user relationships data.
     * 
     * @param userId The user ID
     * @param relationshipsData The relationships data to cache
     */
    public void cacheUserRelationshipsData(Long userId, Object relationshipsData) {
        try {
            String key = USER_RELATIONSHIPS_CACHE_PREFIX + userId;
            redisTemplate.opsForValue().set(key, relationshipsData, USER_RELATIONSHIPS_CACHE_TTL);
            log.debug("Cached user relationships data for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error caching user relationships data for user ID: {}", userId, e);
        }
    }

    /**
     * Get cached user relationships data.
     * 
     * @param userId The user ID
     * @return The cached relationships data or null if not found
     */
    @Cacheable(value = "userRelationships", key = "#userId")
    public Object getCachedUserRelationshipsData(Long userId) {
        try {
            String key = USER_RELATIONSHIPS_CACHE_PREFIX + userId;
            Object relationshipsData = redisTemplate.opsForValue().get(key);
            log.debug("Retrieved cached user relationships data for user ID: {}", userId);
            return relationshipsData;
        } catch (Exception e) {
            log.error("Error retrieving cached user relationships data for user ID: {}", userId, e);
            return null;
        }
    }

    /**
     * Evict user data from cache.
     * 
     * @param userId The user ID
     */
    @CacheEvict(value = "users", key = "#userId")
    public void evictUserData(Long userId) {
        try {
            String key = USER_CACHE_PREFIX + userId;
            redisTemplate.delete(key);
            log.debug("Evicted user data from cache for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error evicting user data from cache for user ID: {}", userId, e);
        }
    }

    /**
     * Evict relationship type data from cache.
     * 
     * @param relationshipTypeId The relationship type ID
     */
    @CacheEvict(value = "relationshipTypes", key = "#relationshipTypeId")
    public void evictRelationshipTypeData(Long relationshipTypeId) {
        try {
            String key = RELATIONSHIP_TYPE_CACHE_PREFIX + relationshipTypeId;
            redisTemplate.delete(key);
            log.debug("Evicted relationship type data from cache for ID: {}", relationshipTypeId);
        } catch (Exception e) {
            log.error("Error evicting relationship type data from cache for ID: {}", relationshipTypeId, e);
        }
    }

    /**
     * Evict user relationships data from cache.
     * 
     * @param userId The user ID
     */
    @CacheEvict(value = "userRelationships", key = "#userId")
    public void evictUserRelationshipsData(Long userId) {
        try {
            String key = USER_RELATIONSHIPS_CACHE_PREFIX + userId;
            redisTemplate.delete(key);
            log.debug("Evicted user relationships data from cache for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error evicting user relationships data from cache for user ID: {}", userId, e);
        }
    }

    /**
     * Clear all cache entries.
     */
    public void clearAllCache() {
        try {
            redisTemplate.getConnectionFactory().getConnection().flushAll();
            log.info("Cleared all cache entries");
        } catch (Exception e) {
            log.error("Error clearing all cache entries", e);
        }
    }
}

