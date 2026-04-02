package com.hls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.PageParam;
import com.hls.base.R;
import com.hls.base.config.UserContext;
import com.hls.base.utils.MqBase;
import com.hls.content.dto.SongListDto;
import com.hls.content.mapper.SongListMapper;
import com.hls.content.po.Song;
import com.hls.content.po.SongList;
import com.hls.content.po.SongSongList;
import com.hls.content.po.TextInfo;
import com.hls.content.service.ISongListService;
import com.hls.content.service.ISongService;
import com.hls.content.service.ISongSongListService;
import com.hls.content.service.ITextInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    private final ISongSongListService songSongListService;
    private final ISongService songService;
    private final MqBase mqBase;
    private final ITextInfoService textInfoService;
    private final ApplicationContext  applicationContext;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R<Object> addSongList(SongList songList) {
        Integer userId = UserContext.getUser();
        songList.setUserId(userId);
        String introduction = songList.getIntroduction();
        String substring = introduction.substring(50);
        if (!substring.isBlank()) {
            TextInfo textInfo = new TextInfo();
            textInfo.setContent(substring);
            textInfoService.save(textInfo);
            songList.setIntroductionId(textInfo.getId());
            songList.setIntroduction(introduction.substring(0, 50));
        }
        save(songList);
        return R.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R<Object> deleteSongList(Integer songListId) {
        SongList songList = getById(songListId);
        if (songList == null) return R.failure();
        LambdaQueryWrapper<SongSongList> qw = new LambdaQueryWrapper<SongSongList>()
                .eq(SongSongList::getSongListId, songList.getId());
        songSongListService.remove(qw);
        mqBase.sendMessageDelMedia(songList.getAvatarId(),songList.getAvatarUrl());
        removeById(songListId);
        return R.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R<Object> updateSongList(SongList songList) {
        SongListServiceImpl bean = applicationContext.getBean(SongListServiceImpl.class);
        bean.deleteSongList(songList.getId());
        bean.save(songList);
        return R.success();
    }

    @Override
    public R<SongListDto> getSongListDetail(Integer songListId) {
        SongList byId = getById(songListId);
        SongListDto songListDto = BeanUtil.copyProperties(byId, SongListDto.class);
        List<Song> list = songSongListService.getSongIdsBySongListId(songListId)
                .stream()
                .map(songService::getById)
                .toList();
        songListDto.setSongList(list);
        return R.success(songListDto);
    }


}
