package com.hls.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hls.po.Fans;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户粉丝表 Mapper 接口
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Mapper
public interface FansMapper extends BaseMapper<Fans> {

}
