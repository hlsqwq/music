package com.hls.content.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hls.content.config.Redis;
import com.hls.content.dto.CategoryTreeDto;
import com.hls.content.mapper.CategoryMapper;
import com.hls.content.po.Category;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 分类表（歌曲/歌单/专辑分类） 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    private final CategoryMapper categoryMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Redis(key = "Category_AllCategory")
    @Override
    public List<CategoryTreeDto> getAllCategory() {
        List<Category> rootCate = categoryMapper.getRootCate();
        ArrayList<CategoryTreeDto> list = new ArrayList<>();
        for (Category c : rootCate) {
            LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<Category>()
                    .likeRight(Category::getPath, c.getPath());
            List<Category> children = list(qw);
            children.removeFirst();
            List<CategoryTreeDto> list1 = children.stream()
                    .map(v -> BeanUtil.copyProperties(v, CategoryTreeDto.class))
                    .toList();

            CategoryTreeDto root = BeanUtil.copyProperties(c, CategoryTreeDto.class);
            root.setChildren(list1);
            list.add(root);
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    @Redis(key = "Category_AllCategory",type = "post")
    @Override
    public void addCategory(Long userId, Long id, String content) {
        //todo
        Category byId = getById(id);
        if(Objects.isNull(byId)){
            return;
        }
        Category category = new Category();
        category.setContent(content);
        save(category);
        category.setPath(byId.getPath()+category.getId()+"/");
        updateById(category);
    }


}
