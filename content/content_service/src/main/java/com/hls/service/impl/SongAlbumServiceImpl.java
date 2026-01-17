package com.hls.service.impl;


import com.hls.mapper.SongAlbumMapper;
import com.hls.po.SongAlbum;
import com.hls.service.ISongAlbumService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌曲-专辑关联表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class SongAlbumServiceImpl extends ServiceImpl<SongAlbumMapper, SongAlbum> implements ISongAlbumService {

}
