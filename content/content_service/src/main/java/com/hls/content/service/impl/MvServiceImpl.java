package com.hls.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.config.UserContext;
import com.hls.base.utils.RedisBase;
import com.hls.content.utils.RedisHotUtil;
import com.hls.content.po.Mv;
import com.hls.content.po.Singer;
import com.hls.content.mapper.MvMapper;
import com.hls.content.mapper.SingerMapper;
import com.hls.content.service.IMvService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.service.ISingerService;
import com.hls.base.utils.AuditState;
import com.hls.base.config.MqConfig;
import com.hls.base.utils.MqBase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 歌手mv 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-20
 */
@RequiredArgsConstructor
@Service
public class MvServiceImpl extends ServiceImpl<MvMapper, Mv> implements IMvService {

    private final SingerMapper singerMapper;
    private final ISingerService singerService;
    private final RedisHotUtil redisHotUtil;
    private final RedisBase redisBase;
    private final DefaultRedisScript<Long> incrOrDecrLike;
    private final MqBase mqBase;

    @Override
    public PageResult<Mv> pageBySinger(Long id, PageParam pageParam) {
        Page<Mv> page = Page.of(pageParam.getNum(), pageParam.getSize());
        LambdaQueryWrapper<Mv> qw = new LambdaQueryWrapper<Mv>()
                .eq(Mv::getSingerId, id)
                .eq(Mv::getStatus, AuditState.pass)
                .orderByDesc(Mv::getPlayNum);
        Page<Mv> res = page(page, qw);

        PageResult<Mv> albumPageResult = new PageResult<>();
        albumPageResult.setNum(res.getCurrent());
        albumPageResult.setSize(res.getSize());
        albumPageResult.setTotal(res.getTotal());
        albumPageResult.setItem(res.getRecords());
        return albumPageResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addMv(Mv mv) {
        save(mv);
        Singer singer = singerMapper.selectById(mv.getSingerId());
        if (singer != null) {
            singer.setMvNum(singer.getMvNum() + 1);
            singerMapper.updateById(singer);
        }
        // 这里可以添加添加媒体的逻辑，根据实际需求实现
        // mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY, mv.getAvatar());
        // mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY, mv.getVideo());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteMv(Integer mvId) {
        Mv mv = getById(mvId);
        if (mv != null) {
            if (mv.getAvatar() != null) {
                mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY, mv.getAvatar());
            }
            if (mv.getVideo() != null) {
                mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY, mv.getVideo());
            }
            removeById(mvId);

            Singer singer = singerMapper.selectById(mv.getSingerId());
            if (singer != null && singer.getMvNum() > 0) {
                singer.setMvNum(singer.getMvNum() - 1);
                singer.setPlayNum(singer.getPlayNum() - mv.getPlayNum());
                singer.setLikeNum(singer.getLikeNum() - mv.getLikeNum());
                singerMapper.updateById(singer);
                // 这里可以添加更新歌手热度的逻辑，根据实际需求实现
                // mqBase.sendMessageToMusic(MqConfig.HOT_SINGER_KEY, singer.getId());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMv(Mv mv) {
        Mv byId = getById(mv.getId());

        if (byId.getAvatar() != null && !byId.getAvatar().equals(mv.getAvatar())) {
            mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY, byId.getAvatar());
        }
        if (byId.getVideo() != null && !byId.getVideo().equals(mv.getVideo())) {
            mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY, byId.getVideo());
        }

        updateById(mv);

        if (mv.getAvatar() != null && !mv.getAvatar().equals(byId.getAvatar())) {
            // 这里可以添加添加媒体的逻辑，根据实际需求实现
            // mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY, mv.getAvatar());
        }
        if (mv.getVideo() != null && !mv.getVideo().equals(byId.getVideo())) {
            // 这里可以添加添加媒体的逻辑，根据实际需求实现
            // mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY, mv.getVideo());
        }

        Singer byId1 = singerService.getById(mv.getSingerId());
        if (byId1 == null) {
            return;
        }
        Long likeNum = mv.getLikeNum() - byId1.getLikeNum();
        Long playNum = mv.getPlayNum() - byId1.getPlayNum();
        byId1.setLikeNum(byId1.getLikeNum() + likeNum);
        byId1.setPlayNum(byId1.getPlayNum() + playNum);
        singerService.updateById(byId1);
        // 这里可以添加更新歌手热度的逻辑，根据实际需求实现
        // mqBase.sendMessageToMusic(MqConfig.HOT_SINGER_KEY, byId1.getId());
    }

    @Override
    public List<Mv> getMvList() {
        LambdaQueryWrapper<Mv> qw = new LambdaQueryWrapper<Mv>()
                .eq(Mv::getStatus, AuditState.pass)
                .orderByDesc(Mv::getCreateTime);
        return list(qw);
    }

    /**
     * 增加MV播放量
     *
     * @param mvId MV ID
     * @return
     */
    @Override
    public Long incrPlayNum(Integer mvId) {
        String key = redisBase.getKey("play", "mv", mvId);
        Long increment = redisBase.increment(key);
        if (increment == 1) {
            Mv byId = getById(mvId);
            redisBase.set(key, byId.getPlayNum() + 1);
            return byId.getPlayNum() + 1;
        }
        return increment;
    }

    /**
     * 增加MV点赞数
     * -- KEYS[1]: userLikeBitmap (位图)
     * -- KEYS[2]: likeCount (计数器)
     * -- KEYS[3]: likeSet (热点ID集合)
     * -- ARGV[1]: userId (偏移量)
     * -- ARGV[2]: mvId (业务ID)
     *
     * @param mvId MV ID
     * @return 增加后的点赞数
     */
    @Override
    public Long incrOrDecrLikeNum(Integer mvId) {
        Integer userId = UserContext.getUser();
        String key1 = redisBase.getKey("userLikeBitmap", "mv");
        String key2 = redisBase.getKey("like", "mv", mvId);
        String key3 = redisBase.getKey("likeSet", "mv");
        return redisBase.executeLua(incrOrDecrLike, List.of(key1, key2, key3), userId, mvId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePlayOrLike(List<Mv> map) {
        for (Mv mv : map) {
            mv.setHot(redisHotUtil.culMvHot(mv.getPlayNum(), mv.getLikeNum(),
                    mv.getFavoriteNum(), mv.getCommentNum()));
        }
        updateBatchById(map);
    }
}
