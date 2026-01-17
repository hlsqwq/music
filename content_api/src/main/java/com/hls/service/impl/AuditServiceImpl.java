package com.hls.service.impl;

import com.hls.po.Audit;
import com.hls.mapper.AuditMapper;
import com.hls.service.IAuditService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
