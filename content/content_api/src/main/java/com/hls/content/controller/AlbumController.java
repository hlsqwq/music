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
     * 获取歌手的专辑
     *
     * @param id        歌手 id
     * @param order     排序字段 hot and createTime
     * @param pageParam 分页信息
     * @return 专辑
     */
    @GetMapping("/page/{id}")
    public PageResult<Album> pageBySingerId(@PathVariable Integer id, String order, @RequestBody PageParam pageParam) {
        return albumService.pageBySingerId(id, order, pageParam);
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
    public void deleteAlbum(@PathVariable Integer albumId) {
        albumService.deleteAlbum(albumId);
    }

    /**
     * 修改专辑
     *
     * @param albumDetailDto 专辑信息
     */
    @Access(value = "deputy")
    @PutMapping("/update")
    public void updateAlbum(@RequestBody AlbumDetailDto albumDetailDto) {
        albumService.updateAlbum(albumDetailDto);
    }

    /**
     * 获取专辑详细信息
     *
     * @param albumId 专辑id
     * @return 专辑详细信息，包含歌曲列表
     */
    @GetMapping("/detail/{albumId}")
    public AlbumDetailDto getAlbumDetail(@PathVariable Integer albumId) {
        return albumService.getAlbumDetail(albumId);
    }


}
