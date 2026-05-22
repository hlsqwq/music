package com.hls.content.controller;


import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.content.dto.EditSingerDto;
import com.hls.content.dto.SingerDto;
import com.hls.base.po.Singer;
import com.hls.content.service.ISingerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
     * 分页查询歌手列表
     *
     * @param pageParam 分页参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public PageResult<Singer> pageList(@RequestBody PageParam pageParam) {
        return singerService.pageList(pageParam);
    }


    /**
     *
     * @param singerDto
     */
    @PostMapping("/add")
    public void add_singer(@RequestBody SingerDto singerDto) {
        singerService.add_singer(singerDto);
    }



    @PutMapping("/update")
    public void update_singer(@RequestBody EditSingerDto editSingerDto) {
        singerService.update_singer(editSingerDto);
    }


    /**
     * 获取歌手信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Singer get_singer(@PathVariable Long id) {
        return singerService.getById(id);
    }



    @DeleteMapping("/{id}")
    public void delete_singer(@PathVariable Integer id) {
        singerService.del_singer(id);
    }



    /**
     * 获取top10歌手
     *
     * @param id 分类id
     * @return list<singer>
     */
    @GetMapping("/top10")
    public R<Object> getTop10(int id) {
        return singerService.getTop10(id);
    }


    /**
     * 获取歌手的粉丝数
     * @param singerId 歌手id
     * @return long
     */
    @GetMapping("/fans/{singerId}")
    public R<Object> getFans(@PathVariable Integer singerId) {
        return singerService.getFans(singerId);
    }


    /**
     * 增加歌手的粉丝
     *
     * @param singerId 歌手id
     * @return 更新后的粉丝数 long
     */
    @PostMapping("/follow/{singerId}")
    public R<Object> follow(@PathVariable Integer singerId) {
        return singerService.follow(singerId);
    }

    /**
     * 取消关注
     * @param singerId 歌手id
     * @return 更新后的粉丝数 long
     */
    @PostMapping("/unfollow/{singerId}")
    public R<Object> unfollow(@PathVariable Integer singerId) {
        return singerService.unfollow(singerId);
    }




}
