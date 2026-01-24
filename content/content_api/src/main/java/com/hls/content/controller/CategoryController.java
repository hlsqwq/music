package com.hls.content.controller;

import com.hls.content.dto.CategoryTreeDto;
import com.hls.content.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
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
    public List<CategoryTreeDto> getAllCategory() {
        return categoryService.getAllCategory();
    }

    /**
     *
     * @param id    父类id
     * @param content
     */
    @PostMapping("add")
    public void addCategory(Long id, String content) {
        // todo: 权限判断
        Long userId = 1L;
        categoryService.addCategory(userId, id, content);
    }

    /**
     *
     * @param id    分类id
     * @param content
     */
    @PutMapping("update")
    public void updateCategory(Long id, String content) {
        // todo: 权限判断
        Long userId = 1L;
        categoryService.updateCategory(userId, id, content);
    }

    @DeleteMapping("delete")
    public void deleteCategory(Long id) {
        // todo: 权限判断
        Long userId = 1L;
        categoryService.deleteCategory(userId, id);
    }

}
