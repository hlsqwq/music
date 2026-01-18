package com.hls.controller;


import com.hls.R;
import com.hls.po.Singer;
import com.hls.service.ISingerService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("/create")
    public R add_singer(@RequestBody Singer singer) {
        Long userId = 1L;
        return singerService.create(userId,singer);
    }

}
