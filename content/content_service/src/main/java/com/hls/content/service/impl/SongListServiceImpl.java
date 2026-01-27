package com.hls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.utils.mqUtils;
import com.hls.content.dto.SongListDto;
import com.hls.content.mapper.SongListMapper;
import com.hls.content.mapper.TextInfoMapper;
import com.hls.content.po.Song;
import com.hls.content.po.SongList;
import com.hls.content.po.SongSongList;
import com.hls.content.po.TextInfo;
import com.hls.content.service.ISongListService;
import com.hls.content.service.ISongService;
import com.hls.content.service.ISongSongListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 歌单信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
@RequiredArgsConstructor
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements ISongListService {


    private final mqUtils mqUtils;
    private final ISongSongListService songSongListService;
    private final ISongService songService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addSongList(SongList songList) {
        save(songList);
        mqUtils.addMedia(songList.getAvatar());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSongList(Integer songListId) {
        removeById(songListId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSongList(SongList songList) {
        SongList byId = getById(songList.getId());
        if(songList.getAvatar()!=null && !songList.getAvatar().equals(byId.getAvatar())){
            mqUtils.delMedia(songList.getAvatar());
            mqUtils.addMedia(songList.getAvatar());
        }
        updateById(songList);
    }

    @Override
    public SongListDto getSongListDetail(Integer songListId) {
        SongList byId = getById(songListId);
        SongListDto songListDto = BeanUtil.copyProperties(byId, SongListDto.class);
        List<Song> list = songSongListService.getSongIdsBySongListId(songListId)
                .stream()
                .map(songService::getById)
                .toList();
        songListDto.setSongList(list);
        return songListDto;
    }
}
