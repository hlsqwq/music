package com.hls.content.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.canal.service.ISongListService;
import com.hls.content.mapper.SongListMapper;
import com.hls.content.po.SongList;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌单信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements ISongListService {

}
