package com.hls.content.controller;

import com.hls.base.PageParam;
import com.hls.base.R;
import com.hls.content.config.Access;
import com.hls.content.dto.SongListDto;
import com.hls.content.po.SongList;
import com.hls.content.service.ISongListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 歌单信息表 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/song-list")
public class SongListController {

    private final ISongListService songListService;

    /**
     * 添加歌单
     *
     * @param songList 歌单信息
     * @return 是否成功
     */
    @PostMapping("/add")
    public R<Object> addSongList(@RequestBody SongList songList) {
        return songListService.addSongList(songList);
    }

    /**
     * 删除歌单
     *
     * @param songListId 歌单ID
     * @return 是否成功
     */
    @DeleteMapping("/delete/{songListId}")
    public R<Object> deleteSongList(@PathVariable Integer songListId) {
        return songListService.deleteSongList(songListId);
    }

    /**
     * 更新歌单
     *
     * @param songList 歌单信息
     * @return 是否成功
     */
    @PutMapping("/update")
    public R<Object> updateSongList(@RequestBody SongList songList) {
        return songListService.updateSongList(songList);

    }

    /**
     * 获取歌单详情
     *
     * @param songListId 歌单ID
     * @return 歌单详情
     */
    @GetMapping("/detail/{songListId}")
    public R<SongListDto> getSongListDetail(@PathVariable Integer songListId) {
        return songListService.getSongListDetail(songListId);
    }

    /**
     * 分页查询歌单
     *
     * @return 歌单列表
     */
    @GetMapping("/list")
    public R<List<SongList>> getSongList(@RequestBody PageParam pageParam) {
        return R.success(songListService.list());
    }

}
