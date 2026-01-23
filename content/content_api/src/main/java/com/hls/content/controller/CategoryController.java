package com.hls.content.controller;


import com.hls.content.dto.CategoryTreeDto;
import com.hls.content.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 分类表（歌曲/歌单/专辑分类） 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/category")
public class CategoryController {


    private final ICategoryService categoryService;


    @GetMapping("list")
    public List<CategoryTreeDto> getAllCategory(){
        return categoryService.getAllCategory();
    }

    @PostMapping("add")
    public void addCategory(Long id,String content){
        Long userId=1L;
        categoryService.addCategory(userId,id,content);
    }

}
