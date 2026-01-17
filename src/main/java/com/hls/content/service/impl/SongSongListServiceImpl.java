package com.hls.content.service.impl;

import com.hls.content.po.SongSongList;
import com.hls.content.mapper.SongSongListMapper;
import com.hls.content.service.ISongSongListService;
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
