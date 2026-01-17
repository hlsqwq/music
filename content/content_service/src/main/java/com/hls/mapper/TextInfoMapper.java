package com.hls.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hls.po.TextInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 文本信息表（歌词/歌手/专辑简介） Mapper 接口
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Mapper
public interface TextInfoMapper extends BaseMapper<TextInfo> {

}
