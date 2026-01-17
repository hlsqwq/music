package com.hls.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hls.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
