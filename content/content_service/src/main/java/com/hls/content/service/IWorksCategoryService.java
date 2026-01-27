package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.content.po.WorksCategory;

/**
 * <p>
 * 作品分类表 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
public interface IWorksCategoryService extends IService<WorksCategory> {
    void updateBySingerId(Integer singerId, Long likeNum, Long playNum);
    void updateBySongId(Integer songId, Long likeNum, Long playNum);
}
