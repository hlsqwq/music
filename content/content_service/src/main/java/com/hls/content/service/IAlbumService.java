package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.dto.AlbumDetailDto;
import com.hls.content.po.Album;
import java.util.List;

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

    /**
     * 添加专辑
     *
     * @param album 专辑信息
     */
    void addAlbum(Album album);

    /**
     * 删除专辑
     *
     * @param albumId id
     */
    void deleteAlbum(Long albumId);

    /**
     * 修改专辑
     *
     * @param album 专辑信息
     */
    void updateAlbum(Album album);

    /**
     * 获取专辑详细信息
     *
     * @param albumId 专辑id
     * @return 专辑详细信息，包含歌曲列表
     */
    AlbumDetailDto getAlbumDetail(Long albumId);

    /**
     * 修改专辑里的歌曲
     *
     * @param albumId  用户id
     * @param songIds 歌曲id列表
     */
    void updateAlbumSongs(Long albumId, List<Long> songIds);
}
