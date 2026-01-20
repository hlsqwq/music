package com.hls.content.service.impl;


import com.hls.content.mapper.AuditHistoryMapper;
import com.hls.content.po.AuditHistory;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.service.IAuditHistoryService;
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
