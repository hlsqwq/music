package com.hls.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hls.content.po.AuditHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 审核历史记录表 Mapper 接口
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Mapper
public interface AuditHistoryMapper extends BaseMapper<AuditHistory> {

}
