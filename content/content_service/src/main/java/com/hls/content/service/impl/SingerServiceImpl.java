package com.hls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.R;
import com.hls.base.config.MqConfig;
import com.hls.base.exception.MusicException;
import com.hls.base.utils.MqBase;
import com.hls.base.utils.RedisBase;
import com.hls.base.utils.RedisKeys;
import com.hls.content.dto.EditSingerDto;
import com.hls.content.dto.SingerDto;
import com.hls.content.mapper.SingerMapper;
import com.hls.base.po.Singer;
import com.hls.content.po.TextInfo;
import com.hls.content.service.ISingerService;
import com.hls.content.service.ITextInfoService;
import com.hls.content.vo.TextInfoVo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 歌手信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@Service
public class SingerServiceImpl extends ServiceImpl<SingerMapper, Singer> implements ISingerService {

    private final ITextInfoService textInfoService;
    private final MqBase mqBase;
    private final ApplicationContext applicationContext;
    private final RedisKeys redisKeys;
    private final RedisBase redisBase;
    private final RedissonClient redissonClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add_singer(SingerDto singerDto) {
        if (singerDto == null) {
            MusicException.cast("对象不可为空");
        }

        Singer singer = BeanUtil.copyProperties(singerDto, Singer.class);

        TextInfoVo textInfoVo = textInfoService.saveContent(singerDto.getIntroduction(), 50);
        singer.setIntroduction(textInfoVo.getText());
        singer.setIntroductionId(textInfoVo.getId());
        save(singer);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update_singer(EditSingerDto editSingerDto) {
        SingerServiceImpl bean = applicationContext.getBean(SingerServiceImpl.class);
        bean.del_singer(editSingerDto.getId());
        bean.add_singer(editSingerDto);
    }

    /**
     * 获取top10歌手
     *
     * @param id 分类id
     * @return list<singer>
     */
    @Override
    public R<Object> getTop10(int id) {
        String singerTop = redisKeys.getSingerTop(id);
        List<Integer> ids = redisBase.getTopN(singerTop, 10).stream()
                .map(String::valueOf)
                .map(Integer::parseInt)
                .toList();
        if (ids.isEmpty()) {
            return R.failure("等待下次更新");
        }
        return R.success(listByIds(ids));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void del_singer(Integer id) {
        Singer byId = getById(id);
        textInfoService.removeById(byId.getIntroductionId());
        String substring = byId.getAvatarUrl().substring(byId.getAvatarUrl().indexOf("/") + 1);
        substring = substring.substring(substring.indexOf("/") + 1);
        mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY,
                new com.hls.base.dto.DelTempMedia(byId.getAvatarId(), null, "music", substring));
        removeById(id);
    }

    /**
     * 获取歌手的粉丝数
     *
     * @param singerId 歌手id
     * @return long
     */
    @Override
    public R<Object> getFans(Integer singerId) {
        String key = redisKeys.getSingerFans(singerId);
        Long num = redisBase.get(key, Long.class);
        if (num != null) {
            return R.success(num);
        }
        String lock = redisBase.getKey("lock", "fans", singerId);
        RLock lock1 = redissonClient.getLock(lock);
        try {
            if (lock1.tryLock(1, TimeUnit.SECONDS)) {
                Singer byId = getById(singerId);
                redisBase.set(key, byId.getFansNum());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock1.isHeldByCurrentThread())
                lock1.unlock();
        }
        num = redisBase.get(key, Long.class);
        if (num == null) {
            return R.failure("请稍后");
        }
        return R.success(num);
    }

    /**
     * 增加歌手的粉丝
     *
     * @param singerId 歌手id
     * @return 更新后的粉丝数 long
     */
    @Override
    public R<Object> follow(Integer singerId) {
        String key = redisKeys.getSingerFans(singerId);
        Long increment = redisBase.increment(key);
        if (increment == 1) {
            Singer byId = getById(singerId);
            long n = byId.getFansNum() + redisBase.get(key, Long.class);
            redisBase.set(key, n);
            return R.success(n);
        }
        return R.success(increment);
    }


    /**
     * 取消关注
     *
     * @param singerId 歌手id
     * @return 更新后的粉丝数 long
     */
    @Override
    public R<Object> unfollow(Integer singerId) {
        String key = redisKeys.getSingerFans(singerId);
        String lock = redisBase.getKey("lock", "unfollow:singer", singerId);
        RLock lock1 = redissonClient.getLock(lock);
        if (lock1.tryLock()) {
            if (redisBase.exist(key)) {
                Long decrement = redisBase.decrement(key);
                return R.success(decrement);
            }
            Singer byId = getById(singerId);
            if (byId.getFansNum() > 0) {
                redisBase.set(key, byId.getFansNum() - 1);
                return R.success(byId.getFansNum() - 1);
            }
            return R.failure();
        }
        return R.failure("请重试");
    }


}
