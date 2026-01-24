package com.hls.content.controller;

import com.hls.content.config.Access;
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
    public List<CategoryTreeDto> getAllCategory() {
        return categoryService.getAllCategory();
    }

    /**
     *
     * @param id      父类id
     * @param content
     */
    @Access(value = "member")
    @PostMapping("add")
    public void addCategory(Long id, String content) {
        Long userId = 1L;
        categoryService.addCategory(userId, id, content);
    }

    /**
     *
     * @param id      分类id
     * @param content
     */
    @Access(value = "member")
    @PutMapping("update")
    public void updateCategory(Long id, String content) {
        Long userId = 1L;
        categoryService.updateCategory(userId, id, content);
    }

    @Access(value = "member")
    @DeleteMapping("delete")
    public void deleteCategory(Long id) {
        Long userId = 1L;
        categoryService.deleteCategory(userId, id);
    }

}
