package com.hls.media.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.R;
import com.hls.media.po.UserMedia;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hls
 * @since 2026-03-30
 */
public interface IUserMediaService extends IService<UserMedia> {
    /**
     * 同步到用户资源表
     *
     * @param mediaId 资源 id
     */
    void saveToDb(Integer mediaId);

    R<Object> del(Integer id);

    Integer ref(Integer mediaId);


}
