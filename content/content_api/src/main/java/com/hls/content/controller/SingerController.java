package com.hls.content.controller;


import com.hls.content.dto.EditSingerDto;
import com.hls.content.dto.SingerDetailDto;
import com.hls.content.dto.SingerDto;
import com.hls.content.po.Singer;
import com.hls.content.service.ISingerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 歌手信息表 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/singer")
public class SingerController {


    private final ISingerService singerService;


    /**
     * 管理端
     * @param singerDto
     */
    @PostMapping("/create")
    public void add_manage_singer(@RequestBody SingerDto singerDto) {
        //todo
        Long userId = 1L;
        singerService.add_singer(userId, singerDto);
    }


    /**
     * 管理端
     * @param editSingerDto
     */
    @PutMapping("/update")
    public void update_manage_singer(EditSingerDto editSingerDto) {
        //todo
        Long userId = 1L;
        singerService.update_singer(userId, editSingerDto);
    }


    @GetMapping("/{id}")
    public Singer get_manage_singer(@PathVariable Long id) {
        return singerService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete_manage_singer(@PathVariable Long id) {
        singerService.removeById(id);
    }



    /**
     * 获取top10歌手
     * @param id 分类id
     * @return
     */
    @GetMapping("/top10")
    public List<EditSingerDto> getTop10(int id) {
        return singerService.getTop10(id);
    }



    @GetMapping("/detail/{id}")
    public Singer get_detail(@PathVariable Long id) {
        return singerService.getById(id);
    }





}
