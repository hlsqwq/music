package com.hls.content.controller;

import com.hls.base.PageParam;
import com.hls.base.PageResult;

import com.hls.content.config.Access;
import com.hls.content.dto.AlbumDetailDto;
import com.hls.content.po.Album;
import com.hls.content.service.IAlbumService;
import com.hls.content.service.ISingerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * <p>
 * 专辑信息表 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/album")
public class AlbumController {

    private final IAlbumService albumService;


    /**
     *
     * @param id    歌手id
     * @param pageParam
     * @return
     */
    @GetMapping("/page/{id}")
    public PageResult<Album> pageBySingerId(@PathVariable Long id, @RequestBody PageParam pageParam) {
        return albumService.pageBySingerId(id, pageParam);
    }

    /**
     * 添加专辑
     * 
     * @param albumDetailDto 专辑详细信息
     */
    @Access(value = "deputy")
    @PostMapping("/add")
    public void addAlbum(@RequestBody AlbumDetailDto albumDetailDto) {
        albumService.addAlbum(albumDetailDto);
    }

    /**
     * 删除专辑
     * 
     * @param albumId 专辑id
     */
    @Access(value = "deputy")
    @DeleteMapping("/delete/{albumId}")
    public void deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
    }

    /**
     * 修改专辑
     * 
     * @param album 专辑信息
     */
    @Access(value = "deputy")
    @PutMapping("/update")
    public void updateAlbum(@RequestBody Album album) {
        albumService.updateAlbum(album);
    }

    /**
     * 获取专辑详细信息
     * 
     * @param albumId 专辑id
     * @return 专辑详细信息，包含歌曲列表
     */
    @GetMapping("/detail/{albumId}")
    public AlbumDetailDto getAlbumDetail(@PathVariable Long albumId) {
        return albumService.getAlbumDetail(albumId);
    }

    /**
     * 修改专辑里的歌曲
     * 
     * @param albumId 专辑id
     * @param songIds 歌曲id列表
     */
    @Access(value = "deputy")
    @PutMapping("/update-songs/{albumId}")
    public void updateAlbumSongs(@PathVariable Long albumId, @RequestBody List<Long> songIds) {
        albumService.updateAlbumSongs(albumId, songIds);
    }

}
