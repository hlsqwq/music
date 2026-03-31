package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.dto.HotDataDto;
import com.hls.content.po.Song;

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
    Long incrPlayNum(Integer songId);


    void updateSongPlay(List<HotDataDto> task);
}
