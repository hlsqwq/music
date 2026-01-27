package com.hls.content.controller;


import com.hls.content.config.Access;
import com.hls.content.service.ISongSongListService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 歌曲-歌单关联表 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RestController
@RequestMapping("/song-song-list")
public class SongSongListController {
    @Resource
    private ISongSongListService songSongListService;

    /**
     * 单个添加歌曲到歌单
     * @param songId 歌曲ID
     * @param songListId 歌单ID
     */
    @Access(value = "member")
    @PostMapping("/add")
    public void addSongToSongList(@RequestParam Integer songId, @RequestParam Integer songListId) {
        songSongListService.addSongToSongList(songId, songListId);
    }

    /**
     * 批量添加歌曲到歌单
     * @param songIds 歌曲ID列表
     * @param songListId 歌单ID
     */
    @Access(value = "member")
    @PostMapping("/batch-add")
    public void batchAddSongsToSongList(@RequestParam List<Integer> songIds, @RequestParam Integer songListId) {
        songSongListService.batchAddSongsToSongList(songIds, songListId);
    }

    /**
     * 单个删除歌单中的歌曲
     * @param songId 歌曲ID
     * @param songListId 歌单ID
     */
    @Access(value = "member")
    @DeleteMapping("/remove")
    public void removeSongFromSongList(@RequestParam Integer songId, @RequestParam Integer songListId) {
        songSongListService.removeSongFromSongList(songId, songListId);
    }

    /**
     * 批量删除歌单中的歌曲
     * @param songIds 歌曲ID列表
     * @param songListId 歌单ID
     */
    @Access(value = "member")
    @DeleteMapping("/batch-remove")
    public void batchRemoveSongsFromSongList(@RequestParam List<Integer> songIds, @RequestParam Integer songListId) {
        songSongListService.batchRemoveSongsFromSongList(songIds, songListId);
    }

    /**
     * 获取歌单中的所有歌曲ID
     * @param songListId 歌单ID
     * @return 歌曲ID列表
     */
    @GetMapping("/song-ids/{songListId}")
    public List<Integer> getSongIdsBySongListId(@PathVariable Integer songListId) {
        return songSongListService.getSongIdsBySongListId(songListId);
    }
}
