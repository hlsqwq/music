package com.hls.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hls.content.po.Media;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 媒体文件信息表 Mapper 接口
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Mapper
public interface MediaMapper extends BaseMapper<Media> {

}
