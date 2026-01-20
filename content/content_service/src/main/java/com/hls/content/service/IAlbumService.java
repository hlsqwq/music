package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.po.Album;

/**
 * <p>
 * 专辑信息表 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
public interface IAlbumService extends IService<Album> {

    PageResult<Album> pageBySingerId(Long id, PageParam pageParam);
}
