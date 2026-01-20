package com.hls.content.controller;


import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.po.Mv;
import com.hls.content.service.IMvService;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * 歌手mv 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-01-20
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/mv")
public class MvController {


    private final IMvService mvService;

    @GetMapping("page/{id}")
    public PageResult<Mv> pageBySinger(@PathVariable Long id, @RequestBody PageParam pageParam) {
        return mvService.pageBySinger(id,pageParam);
    }

}
