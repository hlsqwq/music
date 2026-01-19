package com.hls.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hls.content.po.Album;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 专辑信息表 Mapper 接口
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */

@Mapper
public interface AlbumMapper extends BaseMapper<Album> {

}
