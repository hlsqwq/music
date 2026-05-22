package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.content.dto.SongListDto;
import com.hls.base.po.SongList;

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
    R<Object> addSongList(SongListDto songListDto);

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
    R<Object> updateSongList(SongListDto songListDto);

    /**
     * 获取歌单详情
     *
     * @param songListId 歌单ID
     * @return 歌单详情
     */
    R<SongListDto> getSongListDetail(Integer songListId);

    /**
     * 分页查询歌单列表
     *
     * @param pageParam 分页参数
     * @return 分页结果
     */
    R<PageResult<SongList>> pageList(PageParam pageParam);

    /**
     * 获取歌单热度TopN
     *
     * @param topN 数量
     * @return 歌单列表
     */
    R<List<SongList>> getTopNSongLists(Integer topN);

}
