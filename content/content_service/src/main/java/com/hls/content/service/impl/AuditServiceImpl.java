package com.hls.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.mapper.AuditMapper;
import com.hls.content.po.Album;
import com.hls.content.po.Audit;
import com.hls.content.service.IAuditService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审核申请表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class AuditServiceImpl extends ServiceImpl<AuditMapper, Audit> implements IAuditService {



}
