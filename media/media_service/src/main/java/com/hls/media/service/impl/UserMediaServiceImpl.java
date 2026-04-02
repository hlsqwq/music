package com.hls.media.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.R;
import com.hls.base.config.MqConfig;
import com.hls.base.config.UserContext;
import com.hls.base.utils.MqBase;
import com.hls.media.config.MinioConfig;
import com.hls.base.dto.DelTempMedia;
import com.hls.media.mapper.UserMediaMapper;
import com.hls.media.po.Media;
import com.hls.media.po.UserMedia;
import com.hls.media.service.IMediaService;
import com.hls.media.service.IUserMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-03-30
 */
@RequiredArgsConstructor
@Service
public class UserMediaServiceImpl extends ServiceImpl<UserMediaMapper, UserMedia> implements IUserMediaService {

    @Lazy
    @Autowired
    private IMediaService MediaService;
    private final MqBase mqBase;
    private final MinioConfig minioConfig;

    /**
     * 同步到用户资源表
     *
     * @param mediaId 资源 id
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveToDb(Integer mediaId) {
        Media byId = MediaService.getById(mediaId);
        UserMedia userMedia = BeanUtil.copyProperties(byId, UserMedia.class);
        userMedia.setUserId(UserContext.getUser());
        userMedia.setId(null);
        userMedia.setMediaId(byId.getId());
        userMedia.setMediaUrl(byId.getUrl());
        userMedia.setCreateTime(LocalDateTime.now());
        saveOrUpdate(userMedia);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public R<Object> del(Integer id) {
        Integer userId = UserContext.getUser();
        UserMedia byId = getById(id);
        if (byId == null) {
            return R.failure("没有资源文件");
        }
        if (!Objects.equals(byId.getUserId(), userId)) {
            return R.failure("不可以删除他人资源");
        }
        removeById(id);
        LambdaQueryWrapper<UserMedia> eq = new LambdaQueryWrapper<UserMedia>()
                .eq(UserMedia::getMediaId, byId.getMediaId());
        List<UserMedia> list = list(eq);
        if (list.isEmpty()) {
            Media byId1 = MediaService.getById(byId.getMediaId());
            MediaService.removeById(byId.getMediaId());
            mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY,
                    new DelTempMedia(null,null, minioConfig.music, byId1.getPath()));
        }
        return R.success();
    }

    @Override
    public Integer ref(Integer mediaId) {
        LambdaQueryWrapper<UserMedia> eq = new LambdaQueryWrapper<UserMedia>()
                .eq(UserMedia::getMediaId, mediaId);
        return list(eq).size();
    }


}
