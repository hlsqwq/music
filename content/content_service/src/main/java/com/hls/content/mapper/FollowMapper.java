package com.hls.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hls.content.po.Follow;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户关注表 Mapper 接口
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

}
