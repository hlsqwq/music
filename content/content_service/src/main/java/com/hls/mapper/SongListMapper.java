package com.hls.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hls.po.SongList;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 歌单信息表 Mapper 接口
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Mapper
public interface SongListMapper extends BaseMapper<SongList> {

}
