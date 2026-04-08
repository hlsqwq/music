package com.hls.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.config.UserContext;
import com.hls.base.dto.DelTempMedia;
import com.hls.base.utils.RedisBase;
import com.hls.content.utils.RedisHotUtil;
import com.hls.base.po.Mv;
import com.hls.base.po.Singer;
import com.hls.content.mapper.MvMapper;
import com.hls.content.mapper.SingerMapper;
import com.hls.content.service.IMvService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.service.ISingerService;
import com.hls.base.utils.AuditState;
import com.hls.base.config.MqConfig;
import com.hls.base.utils.MqBase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
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
    private final ApplicationContext applicationContext;

    @Override
    public PageResult<Mv> pageBySinger(Integer id, PageParam pageParam) {
        Page<Mv> page = Page.of(pageParam.getNum(), pageParam.getSize());
        LambdaQueryWrapper<Mv> qw = new LambdaQueryWrapper<Mv>()
                .eq(Mv::getSingerId, id)
                .eq(Mv::getStatus, AuditState.pass)
                .orderByDesc(Mv::getHot);
        Page<Mv> res = page(page, qw);

        PageResult<Mv> albumPageResult = new PageResult<>();
        albumPageResult.setNum(pageParam.getNum());
        albumPageResult.setSize(pageParam.getSize());
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
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteMv(Integer mvId) {
        Mv mv = getById(mvId);
        if (mv == null) {
            return;
        }
        String substring = mv.getAvatarUrl().substring(mv.getAvatarUrl().indexOf("/") + 1);
        substring = substring.substring(substring.indexOf("/") + 1);
        mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY,
                new DelTempMedia(mv.getAvatarId(),null, "music", substring));
        substring = mv.getVideo().substring(mv.getVideo().indexOf("/") + 1);
        substring = substring.substring(substring.indexOf("/") + 1);
        mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY,
                new DelTempMedia(mv.getVideoId(),null, "music", substring));
        removeById(mvId);
        Singer singer = singerMapper.selectById(mv.getSingerId());
        if (singer != null && singer.getMvNum() > 0) {
            singer.setMvNum(singer.getMvNum() - 1);
            singerMapper.updateById(singer);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMv(Mv mv) {
        MvServiceImpl bean = applicationContext.getBean(MvServiceImpl.class);
        bean.deleteMv(mv.getId());
        bean.addMv(mv);
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
