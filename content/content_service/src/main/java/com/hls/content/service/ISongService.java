package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.content.dto.HotDataDto;
import com.hls.content.dto.SongDto;
import com.hls.base.po.Song;

import java.util.List;

/**
 * <p>
 * 歌曲信息表 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
public interface ISongService extends IService<Song> {

    PageResult<Song> pageBySingerId(Long id, PageParam pageParam);

    /**
     * 增加歌曲播放量
     *
     * @param songId 歌曲ID
     * @return
     */
    R<Object> incrPlayNum(Integer songId);

    /**
     * 增加歌曲
     *
     * @param songDto 歌曲信息
     * @return
     */
    R<String> addSong(SongDto songDto);

    /**
     * 删除歌曲
     *
     * @param songId 歌曲ID
     * @return 是否成功
     */
    R<String> deleteSong(Integer songId);

    /**
     * 获取歌曲TopN
     *
     * @param topN 数量
     * @return 歌曲列表
     */
    R<List<Song>> getTopNSongs(Integer topN);

    /**
     * 更新歌曲
     * @param songDto 歌曲信息
     * @return 是否成功
     */
    R<Object> updateSong(SongDto songDto);

    void updateSongPlay(List<HotDataDto> task);
}
