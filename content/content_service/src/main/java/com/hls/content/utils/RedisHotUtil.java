package com.hls.content.utils;

import com.hls.base.utils.RedisBase;
import com.hls.base.utils.RedisKeys;
import com.hls.content.dto.HotDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisHotUtil {


    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<Integer> hotTask;
    private final RedisBase redisBase;

    /**
     * 计算热度
     *
     * @param play     播放量
     * @param like     点赞数
     * @param favorite 收藏数
     * @param comment  评论数
     * @param fans     粉丝数
     * @return 热度
     */
    public Long culHot(long play, long like, long favorite, long comment, long fans) {
        return (long) (favorite * 0.2 + comment * 0.3 + fans * 0.2 + play * 0.1 + like * 0.2);
    }

    public Long culAlbumHot(long favorite) {
        return culHot(0, 0, favorite, 0, 0);
    }

    public Long culMvHot(long play, long like, long favorite, long comment) {
        return culHot(play, like, favorite, comment, 0);
    }

    public Long culSingerHot(long fans) {
        return culHot(0, 0, 0, 0, fans);
    }

    public Long culSongHot(long play, long favorite, long comment) {
        return culHot(play, 0, favorite, comment, 0);
    }

    /**
     * 计算评论热度
     *
     * @param like 点赞数
     * @return 热度
     */
    public double culCommentHot(long like) {
        return culMvHot(0, like, 0, 0);
    }

    /**
     * 增加评论点赞数
     *
     * @param commentId 评论ID
     * @return 最新点赞数
     */
    public long incrCommentLike(Long commentId) {
        String key = "comment:like:" + commentId;
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 获取评论点赞数
     *
     * @param commentId 评论ID
     * @return 点赞数
     */
    public long getCommentLike(Long commentId) {
        String key = "comment:like:" + commentId;
        Long like = redisBase.get(key, Long.class);
        return like != null ? like : 0;
    }


    public List<HotDataDto> getTask(int index, int total, RedisKeys.TableType type,
                                    RedisKeys.DoType countType) {
        List<HotDataDto> ans = new ArrayList<>();
        String key1 = redisBase.getKey(countType.getDoName() + "Set", type.getTableName());
        Set<Object> list = redisTemplate.opsForSet().members(key1);
        if (list == null || list.isEmpty()) {
            return ans;
        }
        list.stream().map(v -> Integer.parseInt(v.toString()))
                .filter(v -> v % total == index)
                .forEach(v -> {
                    String key2 = redisBase.getKey(countType.getDoName(), type.getTableName(), v);
                    Long num = redisBase.get(key2, Long.class);
                    redisBase.delSet(key1, v);
                    HotDataDto hotDataDto = new HotDataDto();
                    hotDataDto.setId(v);
                    hotDataDto.setType(type);
                    switch (countType) {
                        case fans -> hotDataDto.setFansNum(num);
                        case favorite -> hotDataDto.setFavoriteNum(num);
                        case comment -> hotDataDto.setCommentNum(num);
                        case play -> hotDataDto.setPlayNum(num);
                        case like -> hotDataDto.setLikeNum(num);
                    }
                    ans.add(hotDataDto);
                });
        return ans;
    }
}