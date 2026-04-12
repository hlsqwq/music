package com.hls.media.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.R;
import com.hls.base.config.MqConfig;
import com.hls.base.config.UserContext;
import com.hls.base.exception.MusicException;
import com.hls.base.po.UserInfo;
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
        UserInfo user = UserContext.getUser();
        userMedia.setUserId(user.getId());
        userMedia.setId(null);
        userMedia.setMediaId(byId.getId());
        userMedia.setMediaUrl(byId.getUrl());
        userMedia.setCreateTime(LocalDateTime.now());
        saveOrUpdate(userMedia);
    }


    /**
     * 删除用户media
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer del(Integer id) {
        UserMedia byId = getById(id);
        if (byId == null) {
            MusicException.cast("没有资源文件");
            return -1;
        }
        removeById(id);
        Media byId2 = MediaService.getById(byId.getMediaId());
        byId2.setRefCount(byId2.getRefCount() - 1);
        if (byId2.getRefCount() > 0) {
            MediaService.updateById(byId2);
        } else {
            MediaService.removeById(byId.getMediaId());
        }
        return byId2.getRefCount();
    }

    @Override
    public Integer ref(Integer mediaId) {
        LambdaQueryWrapper<UserMedia> eq = new LambdaQueryWrapper<UserMedia>()
                .eq(UserMedia::getMediaId, mediaId);
        return list(eq).size();
    }


}
