package com.hls.base.utils;

import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hls.base.config.RedissonConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisBase {

    @Getter
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper; // 用于手动对象转换
    private final RedissonClient redissonClient;


    public void lock(String key, Runnable runnable) {
        RLock lock = redissonClient.getLock(key);
        boolean isLock = lock.tryLock();
        if (!isLock) {
            log.debug("锁 {} 已被占用，跳过执行", key);
            return;
        }
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public String getKey(String prefix, String table, Object id) {
        return String.format("%s:%s:%s", prefix, table, id.toString());
    }

    public String getKey(String prefix, String table) {
        return String.format("%s:%s", prefix, table);
    }

    public boolean exist(String key) {
        return redisTemplate.hasKey(key);
    }

    public void set(String key, Object value, Long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, getTime(timeout), unit);
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, getTime(null), TimeUnit.HOURS);
    }

    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;
        return objectMapper.convertValue(value, clazz);
    }

    public void expire(String key) {
        expire(key, getTime(null), TimeUnit.HOURS);
    }

    public void expire(String key, TimeUnit unit) {
        expire(key, getTime(null), unit);
    }

    public void expire(String key, Long timeout, TimeUnit unit) {
        redisTemplate.expire(key, getTime(timeout), unit);
    }

    public long getTime(Long time) {
        if (time == null)
            time = 12L;
        return time + RandomUtil.randomLong(12);
    }

    public void del(String key) {
        redisTemplate.delete(key);
    }

    public <T> List<T> getBatch(List<String> keys, Class<T> clazz) {
        List<Object> objects = redisTemplate.opsForValue().multiGet(keys);
        if (objects != null && !objects.isEmpty()) {
            return objects.stream().map(v -> objectMapper.convertValue(v, clazz)).toList();
        }
        return null;
    }

    public void setBatch(HashMap<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    public void setBatch(List<String> keys, List<Object> values) {
        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }
        setBatch(map);
    }

    // --- 2. 原子计数 (点赞、播放量常用) ---
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    // --- 3. BitMap 操作 (用户去重、签到、点赞状态) ---
    public Boolean setBit(String key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    public Boolean getBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    public Long addSet(String key, Object value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    public Boolean containsSet(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Long delSet(String key, Object value) {
        return redisTemplate.opsForSet().remove(key, value);
    }

    // --- 4. 统一执行 Lua 脚本 (最核心的封装) ---
    public <T> T executeLua(DefaultRedisScript<T> script, List<String> keys, Object... args) {
        try {
            return redisTemplate.execute(script, keys, args);
        } catch (Exception e) {
            log.error("Redis Lua 脚本执行失败: {}", e.getMessage());
            return null;
        }
    }


    public boolean zSetExist(String key, int value) {
        Long rank = redisTemplate.opsForZSet().rank(key, value);
        return rank != null && rank > 0;
    }

    public void zSetAdd(String key, int value, double score) {
        if (key.isEmpty()) {
            return;
        }
        ZSetOperations<String, Object> z = redisTemplate.opsForZSet();
        z.add(key, value, score);
    }

    public void zSetAddBatch(String key, Set<ZSetOperations.TypedTuple<Object>> value) {
        if (key.isEmpty()) {
            return;
        }
        ZSetOperations<String, Object> z = redisTemplate.opsForZSet();
        z.add(key, value);
    }

    public Long zSetSize(String key) {
        ZSetOperations<String, Object> z = redisTemplate.opsForZSet();
        return z.size(key);
    }

    public void zSetDelete(String key, int value) {
        if (key.isEmpty()) {
            return;
        }
        ZSetOperations<String, Object> z = redisTemplate.opsForZSet();
        z.remove(key, value);
    }


    /**
     * 删除末尾
     *
     * @param key  redis
     * @param tail 保留的个数
     */
    public void zSetDeleteTail(String key, Integer tail) {
        Long size = zSetSize(key);
        if (size > tail) {
            // 删除排名 0 到 (size - 501) 的元素，剩下的就是 Top 500
            redisTemplate.opsForZSet().removeRange(key, 0, size - tail - 1);
        }
    }

    /**
     * key  categoryId_id_song/singer
     * value    id(songId/singerId)
     * score    hot
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> getTopRange(String key, int start, int end) {
        ZSetOperations<String, Object> z = redisTemplate.opsForZSet();
        return z.reverseRange(key, start, end);
    }


    /**
     * key  categoryId_id_song/singer
     * value    id(songId/singerId)
     * score    hot
     *
     * @param key
     * @param num
     * @return
     */
    public Set<Object> getTopN(String key, int num) {
        return getTopRange(key, 0, num - 1);
    }
}