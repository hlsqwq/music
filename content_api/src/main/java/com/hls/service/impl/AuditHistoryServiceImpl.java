package com.hls.service.impl;

import com.hls.po.AuditHistory;
import com.hls.mapper.AuditHistoryMapper;
import com.hls.service.IAuditHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审核历史记录表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class AuditHistoryServiceImpl extends ServiceImpl<AuditHistoryMapper, AuditHistory> implements IAuditHistoryService {

}
