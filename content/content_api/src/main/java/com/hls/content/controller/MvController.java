package com.hls.content.controller;

import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.config.Access;
import com.hls.content.po.Mv;
import com.hls.content.service.IMvService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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


    /**
     * 分页查询歌手的mv
     * @param id 歌手id
     * @param pageParam 分页参数
     * @return
     */
    @GetMapping("page/{id}")
    public PageResult<Mv> pageBySinger(@PathVariable Integer id, @RequestBody PageParam pageParam) {
        return mvService.pageBySinger(id, pageParam);
    }



    @PostMapping("/add")
    public void addMv(@RequestBody Mv mv) {
        mvService.addMv(mv);
    }

    /**
     * 删除MV
     * 
     * @param mvId MV ID
     */
    @DeleteMapping("/delete/{mvId}")
    public void deleteMv(@PathVariable Integer mvId) {
        mvService.deleteMv(mvId);
    }

    /**
     * 修改MV
     * 
     * @param mv MV信息
     */
    @PutMapping("/update")
    public void updateMv(@RequestBody Mv mv) {
        mvService.updateMv(mv);
    }


    /**
     * 获取MV列表
     * 
     * @return MV列表
     */
    @GetMapping("/list")
    public List<Mv> getMvList() {
        return mvService.getMvList();
    }

}
