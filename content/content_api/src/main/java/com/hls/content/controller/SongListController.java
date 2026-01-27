package com.hls.content.controller;


import com.hls.content.config.Access;
import com.hls.content.dto.SongListDto;
import com.hls.content.po.SongList;
import com.hls.content.service.ISongListService;
import jakarta.annotation.Resource;
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
@RestController
@RequestMapping("/song-list")
public class SongListController {


    @Resource
    private ISongListService songListService;

    /**
     * 添加歌单
     *
     * @param songList 歌单信息
     */
    @Access(value = "member")
    @PostMapping("/add")
    public void addSongList(@RequestBody SongList songList) {
        songListService.addSongList(songList);
    }

    /**
     * 删除歌单
     *
     * @param songListId 歌单ID
     */
    @Access(value = "member")
    @DeleteMapping("/delete/{songListId}")
    public void deleteSongList(@PathVariable Integer songListId) {
        songListService.deleteSongList(songListId);
    }

    /**
     * 更新歌单
     *
     * @param songList 歌单信息
     */
    @Access(value = "member")
    @PutMapping("/update")
    public void updateSongList(@RequestBody SongList songList) {
        songListService.updateSongList(songList);
    }

    /**
     * 获取歌单详情
     *
     * @param songListId 歌单ID
     * @return 歌单详情
     */
    @GetMapping("/detail/{songListId}")
    public SongListDto getSongListDetail(@PathVariable Integer songListId) {
        return songListService.getSongListDetail(songListId);
    }

}
