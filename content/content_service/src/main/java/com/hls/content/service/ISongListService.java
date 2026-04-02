package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.PageParam;
import com.hls.base.R;
import com.hls.content.dto.SongListDto;
import com.hls.content.po.SongList;

import java.util.List;

/**
 * <p>
 * 歌单信息表 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
public interface ISongListService extends IService<SongList> {

    /**
     * 新增歌单
     *
     * @param songList 歌单信息
     * @return
     */
    R<Object> addSongList(SongList songList);

    /**
     * 删除歌单
     *
     * @param songListId 歌单ID
     * @return
     */
    R<Object> deleteSongList(Integer songListId);

    /**
     * 更新歌单
     *
     * @param songList 歌单信息
     * @return
     */
    R<Object> updateSongList(SongList songList);

    /**
     * 获取歌单详情
     *
     * @param songListId 歌单ID
     * @return 歌单详情
     */
    R<SongListDto> getSongListDetail(Integer songListId);

}
