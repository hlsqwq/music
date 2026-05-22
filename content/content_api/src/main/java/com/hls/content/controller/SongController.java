package com.hls.content.controller;


import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.content.dto.SongDto;
import com.hls.base.po.Song;
import com.hls.content.service.ISongService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 歌曲信息表 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/song")
public class SongController {


    private final ISongService songService;



    @PostMapping
    public R<PageResult<Song>> getSongs(@RequestBody PageParam pageParam) {
        return songService.getSongs(pageParam);
    }


    /**
     * 分页查询歌手歌曲
     *
     * @param id 歌手id
     * @return
     */
    @PostMapping("/page/{id}")
    public PageResult<Song> pageBySingerId(@PathVariable Long id, @RequestBody PageParam pageParam) {
        return songService.pageBySingerId(id, pageParam);
    }


    /**
     * 增加歌曲播放量
     *
     * @param songId 歌曲id
     * @return 播放量 long
     */
    @PostMapping("/play/{songId}")
    public R<Object> playSong(@PathVariable Integer songId) {
        return songService.incrPlayNum(songId);
    }

    /**
     * 增加歌曲
     *
     * @param songDto 歌曲信息
     * @return
     */
    @PostMapping("/add")
    public R<String> addSong(@RequestBody SongDto songDto) {
        return songService.addSong(songDto);
    }

    /**
     * 删除歌曲
     *
     * @param songId 歌曲id
     * @return 是否成功
     */
    @DeleteMapping("/delete/{songId}")
    public R<String> deleteSong(@PathVariable Integer songId) {
        return songService.deleteSong(songId);
    }

    /**
     * 获取歌曲TopN
     *
     * @param topN 数量
     * @return 歌曲列表
     */
    @GetMapping("/top/{topN}")
    public R<List<Song>> getTopNSongs(@PathVariable Integer topN) {
        return songService.getTopNSongs(topN);
    }

    /**
     * 更新歌曲
     *
     * @param songDto 歌曲信息
     * @return 是否成功
     */
    @PutMapping("/update")
    public R<Object> updateSong(@RequestBody SongDto songDto) {
        return songService.updateSong(songDto);
    }

}
