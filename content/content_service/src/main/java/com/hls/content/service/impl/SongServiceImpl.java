package com.hls.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.base.dto.DelTempMedia;
import com.hls.base.utils.RedisKeys;
import com.hls.content.dto.HotDataDto;
import com.hls.base.utils.RedisBase;
import com.hls.content.dto.SongDto;
import com.hls.content.po.TextInfo;
import com.hls.content.service.ITextInfoService;
import com.hls.content.utils.RedisHotUtil;
import com.hls.content.mapper.SongMapper;
import com.hls.base.po.Song;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.service.ISongService;
import com.hls.base.utils.AuditState;
import com.hls.base.config.MqConfig;
import com.hls.base.utils.MqBase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
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
    private final RedisKeys redisKeys;
    private final MqBase mqBase;
    private final ITextInfoService textInfoService;
    private final ApplicationContext applicationContext;

    @Override
    public PageResult<Song> pageBySingerId(Long id, PageParam pageParam) {
        Page<Song> page = Page.of(pageParam.getNum(), pageParam.getSize());
        LambdaQueryWrapper<Song> qw = new LambdaQueryWrapper<Song>()
                .eq(Song::getSingerId, id)
                .eq(Song::getStatus, AuditState.pass)
                .orderByDesc(Song::getHot);
        Page<Song> res = page(page, qw);
        PageResult<Song> songPageResult = new PageResult<>();
        songPageResult.setTotal(res.getTotal());
        songPageResult.setItem(res.getRecords());
        songPageResult.setNum(pageParam.getNum());
        songPageResult.setSize(pageParam.getSize());
        return songPageResult;
    }

    /**
     * 增加歌曲播放量
     *
     * @param songId 歌曲ID
     * @return 播放量
     */
    @Override
    public R<Object> incrPlayNum(Integer songId) {
        String key = redisKeys.getSongPlay(songId);
        Long increment = redisBase.increment(key);
        if (increment == 1) {
            Song byId = getById(songId);
            long value = byId.getPlayNum() + redisBase.get(key, Long.class);
            redisBase.set(key, value);
            return R.success(value);
        }
        return R.success(increment);
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

    @Override
    public R<PageResult<Song>> getSongs(PageParam pageParam) {
        Page<Song> objectPage = Page.of(pageParam.getNum(), pageParam.getSize());
        Page<Song> page = page(objectPage);
        PageResult<Song> songPageResult = new PageResult<>();
        songPageResult.setTotal(page.getTotal());
        songPageResult.setItem(page.getRecords());
        songPageResult.setSize(pageParam.getSize());
        songPageResult.setNum(pageParam.getNum());
        return R.success(songPageResult);
    }

    /**
     * 增加歌曲
     *
     * @param songDto 歌曲信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> addSong(SongDto songDto) {
        if (songDto.getLyric().isBlank()) {
            return R.failure("请添加歌词");
        }
        TextInfo textInfo1 = new TextInfo();
        textInfo1.setContent(songDto.getLyric());
        textInfoService.save(textInfo1);
        String introduction = songDto.getIntroduction();
        String substring = introduction.substring(50);
        if (!substring.isBlank()) {
            TextInfo textInfo = new TextInfo();
            textInfo.setContent(substring);
            textInfoService.save(textInfo);
            songDto.setIntroduction(introduction.substring(0, 50));
        }
        save(songDto);
        return R.success(null);
    }

    /**
     * 删除歌曲
     *
     * @param songId 歌曲ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> deleteSong(Integer songId) {
        // 获取歌曲信息
        Song song = getById(songId);
        if (song == null) {
            return R.failure("没有这个对象");
        }

        textInfoService.removeById(song.getIntroductionId());
        textInfoService.removeById(song.getLyricId());
        String substring = song.getAvatarUrl().substring(song.getAvatarUrl().indexOf("/") + 1);
        substring = substring.substring(substring.indexOf("/") + 1);
        mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY,
                new DelTempMedia(song.getAvatarId(), null, "music", substring));
        substring = song.getMusicUrl().substring(song.getMusicUrl().indexOf("/") + 1);
        substring = substring.substring(substring.indexOf("/") + 1);
        mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY,
                new DelTempMedia(song.getMusicId(), null, "music", substring));

        removeById(songId);
        return R.success(null);
    }

    /**
     * 获取歌曲TopN
     *
     * @param topN 数量
     * @return 歌曲列表
     */
    @Override
    public R<List<Song>> getTopNSongs(Integer topN) {
        String songTop = redisKeys.getSongTop();
        List<Integer> ids = redisBase.getTopN(songTop, 10).stream()
                .map(String::valueOf)
                .map(Integer::parseInt)
                .toList();
        if (ids.isEmpty()) {
            return R.failure("等待下次更新");
        }
        return R.success(listByIds(ids));
    }

    /**
     * 更新歌曲
     *
     * @param songDto 歌曲信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public R<Object> updateSong(SongDto songDto) {
        Song oldSong = getById(songDto.getId());
        if (oldSong == null) {
            return R.failure();
        }
        SongServiceImpl bean = applicationContext.getBean(SongServiceImpl.class);
        bean.deleteSong(songDto.getId());
        bean.addSong(songDto);
        return R.success();
    }

}
