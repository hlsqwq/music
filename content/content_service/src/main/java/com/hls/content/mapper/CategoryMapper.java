package com.hls.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hls.content.po.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 分类表（歌曲/歌单/专辑分类） Mapper 接口
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    List<Category> getRootCate();
}
