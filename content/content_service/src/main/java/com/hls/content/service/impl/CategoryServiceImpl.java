package com.hls.content.service.impl;



import com.hls.content.mapper.CategoryMapper;
import com.hls.content.po.Category;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.service.ICategoryService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 分类表（歌曲/歌单/专辑分类） 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
