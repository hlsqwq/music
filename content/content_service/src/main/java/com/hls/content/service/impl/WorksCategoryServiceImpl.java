package com.hls.content.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.mapper.WorksCategoryMapper;
import com.hls.content.po.WorksCategory;
import com.hls.content.service.IWorksCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 作品分类表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class WorksCategoryServiceImpl extends ServiceImpl<WorksCategoryMapper, WorksCategory> implements IWorksCategoryService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBySingerId(Integer singerId, Long likeNum, Long playNum) {
        LambdaQueryWrapper<WorksCategory> eq = new LambdaQueryWrapper<WorksCategory>()
                .eq(WorksCategory::getSingerId, singerId);
        WorksCategory one = getOne(eq);
        if (one == null) {
            return;
        }
        one.setHot((long) (likeNum*0.5+playNum*0.5));
        updateById(one);
    }

    @Override
    public void updateBySongId(Integer songId, Long likeNum, Long playNum) {
        LambdaQueryWrapper<WorksCategory> eq = new LambdaQueryWrapper<WorksCategory>()
                .eq(WorksCategory::getSongId, songId);
        WorksCategory one = getOne(eq);
        if (one == null) {
            return;
        }
        one.setHot((long) (likeNum*0.5+playNum*0.5));
        updateById(one);
    }
}
