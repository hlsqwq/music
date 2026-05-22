package com.hls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.base.config.UserContext;
import com.hls.base.po.UserInfo;
import com.hls.base.utils.MqBase;
import com.hls.base.utils.RedisBase;
import com.hls.base.utils.RedisKeys;
import com.hls.content.dto.SongListDto;
import com.hls.content.mapper.SongListMapper;
import com.hls.base.po.Song;
import com.hls.base.po.SongList;
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
    private final RedisKeys redisKeys;
    private final RedisBase redisBase;
    private final ApplicationContext  applicationContext;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R<Object> addSongList(SongListDto songListDto) {
        UserInfo user = UserContext.getUser();
        Integer userId = user.getId();
        songListDto.setUserId(userId);
        String introduction = songListDto.getIntroduction();
        if (introduction != null && introduction.length() > 50) {
            String substring = introduction.substring(50);
            if (!substring.isBlank()) {
                TextInfo textInfo = new TextInfo();
                textInfo.setContent(substring);
                textInfoService.save(textInfo);
                songListDto.setIntroductionId(textInfo.getId());
                songListDto.setIntroduction(introduction.substring(0, 50));
            }
        }
        save(songListDto);
        // 保存歌曲关联
        if (songListDto.getSongIds() != null && !songListDto.getSongIds().isEmpty()) {
            songSongListService.batchAddSongsToSongList(songListDto.getSongIds(), songListDto.getId());
        }
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
    public R<Object> updateSongList(SongListDto songListDto) {
        SongListServiceImpl bean = applicationContext.getBean(SongListServiceImpl.class);
        bean.deleteSongList(songListDto.getId());
        bean.save(songListDto);
        // 保存歌曲关联
        if (songListDto.getSongIds() != null && !songListDto.getSongIds().isEmpty()) {
            songSongListService.batchAddSongsToSongList(songListDto.getSongIds(), songListDto.getId());
        }
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

    /**
     * 分页查询歌单列表
     *
     * @param pageParam 分页参数
     * @return 分页结果
     */
    @Override
    public R<PageResult<SongList>> pageList(PageParam pageParam) {
        Page<SongList> page = Page.of(pageParam.getNum(), pageParam.getSize());
        LambdaQueryWrapper<SongList> qw = new LambdaQueryWrapper<SongList>()
                .orderByDesc(SongList::getHot);
        Page<SongList> res = page(page, qw);
        PageResult<SongList> result = new PageResult<>();
        result.setTotal(res.getTotal());
        result.setItem(res.getRecords());
        result.setNum(pageParam.getNum());
        result.setSize(pageParam.getSize());
        return R.success(result);
    }

    /**
     * 获取歌单热度TopN
     *
     * @param topN 数量
     * @return 歌单列表
     */
    @Override
    public R<List<SongList>> getTopNSongLists(Integer topN) {
        String songListTop = redisKeys.getSongListTop();
        List<Integer> ids = redisBase.getTopN(songListTop, topN).stream()
                .map(String::valueOf)
                .map(Integer::parseInt)
                .toList();
        if (ids.isEmpty()) {
            return R.failure("等待下次更新");
        }
        return R.success(listByIds(ids));
    }


}
