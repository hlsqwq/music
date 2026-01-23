package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.content.dto.CategoryTreeDto;
import com.hls.content.po.Category;

import java.util.List;

/**
 * <p>
 * 分类表（歌曲/歌单/专辑分类） 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
public interface ICategoryService extends IService<Category> {

    List<CategoryTreeDto> getAllCategory();

    void addCategory(Long userId, Long id, String content);
}
