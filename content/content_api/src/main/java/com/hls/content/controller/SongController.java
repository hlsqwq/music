package com.hls.content.controller;


import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.dto.EditSingerDto;
import com.hls.content.po.Song;
import com.hls.content.service.ISongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    /**
     * 分页查询歌手歌曲
     * @param id    歌手id
     * @return
     */
    @GetMapping("/page/{id}")
    public PageResult<Song> pageBySingerId(@PathVariable Long id, @RequestBody PageParam pageParam) {
        return songService.pageBySingerId(id,pageParam);
    }






}
