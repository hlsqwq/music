package com.hls.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.dto.HotDataDto;
import com.hls.base.utils.RedisBase;
import com.hls.content.utils.RedisHotUtil;
import com.hls.content.mapper.SongMapper;
import com.hls.content.po.Song;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.service.ISongService;
import com.hls.base.utils.AuditState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 歌曲信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements ISongService {

    private final RedisHotUtil redisHotUtil;
    private final RedisBase redisBase;

    private final String playPrefix = "play";
    private final String songTable = "song";

    @Override
    public PageResult<Song> pageBySingerId(Long id, PageParam pageParam) {
        Page<Song> page = Page.of(pageParam.getNum(), pageParam.getSize());
        LambdaQueryWrapper<Song> qw = new LambdaQueryWrapper<Song>()
                .eq(Song::getSingerId, id)
                .eq(Song::getStatus, AuditState.pass)
                .orderByDesc(Song::getLikeNum);
        Page<Song> res = page(page, qw);
        PageResult<Song> songPageResult = new PageResult<>();
        songPageResult.setTotal(res.getTotal());
        songPageResult.setItem(res.getRecords());
        songPageResult.setNum(res.getCurrent());
        songPageResult.setSize(res.getSize());
        return songPageResult;
    }

    /**
     * 增加歌曲播放量
     *
     * @param songId 歌曲ID
     * @return 播放量
     */
    @Override
    public Long incrPlayNum(Integer songId) {
        String key = redisBase.getKey(playPrefix, songTable, songId);
        Long increment = redisBase.increment(key);
        if (increment == 1) {
            Song byId = getById(songId);
            redisBase.set(key, byId.getPlayNum() + 1);
            return byId.getPlayNum() + 1;
        }
        return increment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSongPlay(List<HotDataDto> task) {
        List<Song> list = task.stream().map(v -> {
            Song byId = getById(v.getId());
            byId.setPlayNum(byId.getPlayNum() + v.getPlayNum());
            byId.setHot(redisHotUtil.culSongHot(byId.getPlayNum(), byId.getFavoriteNum(), byId.getCommentNum()));
            return byId;
        }).toList();
        updateBatchById(list);
    }

}
