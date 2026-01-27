package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.content.po.SongSongList;

import java.util.List;

/**
 * <p>
 * 歌曲-歌单关联表 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
public interface ISongSongListService extends IService<SongSongList> {

    /**
     * 单个添加歌曲到歌单
     * @param songId 歌曲ID
     * @param songListId 歌单ID
     */
    void addSongToSongList(Integer songId, Integer songListId);

    /**
     * 批量添加歌曲到歌单
     * @param songIds 歌曲ID列表
     * @param songListId 歌单ID
     */
    void batchAddSongsToSongList(List<Integer> songIds, Integer songListId);

    /**
     * 单个删除歌单中的歌曲
     * @param songId 歌曲ID
     * @param songListId 歌单ID
     */
    void removeSongFromSongList(Integer songId, Integer songListId);

    /**
     * 批量删除歌单中的歌曲
     * @param songIds 歌曲ID列表
     * @param songListId 歌单ID
     */
    void batchRemoveSongsFromSongList(List<Integer> songIds, Integer songListId);

    /**
     * 获取歌单中的所有歌曲ID
     * @param songListId 歌单ID
     * @return 歌曲ID列表
     */
    List<Integer> getSongIdsBySongListId(Integer songListId);

}
