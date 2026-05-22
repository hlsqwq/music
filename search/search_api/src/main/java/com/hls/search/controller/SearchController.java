package com.hls.search.controller;

import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 *
 * @author hls
 * @since 2026-04-07
 */
@RequiredArgsConstructor
@RestController
public class SearchController {

    private final SearchService searchService;

    /**
     * 综合搜索
     *
     * @param keyword 关键词
     * @return 各类型搜索结果
     */
    @GetMapping("/all")
    public R<Map<String, List<Object>>> findAll(@RequestParam String keyword) {
        return searchService.findAll(keyword);
    }

    /**
     * 单类型搜索
     *
     * @param type      搜索类型 (song/singer/mv/album/songlist)
     * @param keyword   关键词
     * @param order     排序字段 (hot/createTime)
     * @param pageParam 分页参数
     * @return 分页结果
     */
    @GetMapping("/{type}")
    public R<PageResult<Object>> findByType(@PathVariable String type,
                                            @RequestParam String keyword,
                                            @RequestParam(required = false) String order,
                                            PageParam pageParam) {
        return searchService.findByType(type, keyword, order, pageParam);
    }
}
