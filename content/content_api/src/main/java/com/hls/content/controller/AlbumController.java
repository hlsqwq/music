package com.hls.content.controller;


import com.hls.base.PageParam;
import com.hls.base.PageResult;

import com.hls.content.po.Album;
import com.hls.content.service.IAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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



    @GetMapping("/page/{id}")
    public PageResult<Album> pageBySingerId(@PathVariable Long id, @RequestBody PageParam pageParam) {
        return albumService.pageBySingerId(id,pageParam);
    }
}
