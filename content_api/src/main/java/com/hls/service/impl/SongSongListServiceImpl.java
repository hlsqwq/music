package com.hls.service.impl;

import com.hls.po.SongSongList;
import com.hls.mapper.SongSongListMapper;
import com.hls.service.ISongSongListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌曲-歌单关联表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class SongSongListServiceImpl extends ServiceImpl<SongSongListMapper, SongSongList> implements ISongSongListService {

}
