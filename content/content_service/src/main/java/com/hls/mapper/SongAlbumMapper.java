package com.hls.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hls.po.SongAlbum;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 歌曲-专辑关联表 Mapper 接口
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Mapper
public interface SongAlbumMapper extends BaseMapper<SongAlbum> {

}
