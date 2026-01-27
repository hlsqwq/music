package com.hls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.MusicCd;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.Status;
import com.hls.base.config.messageConfig;
import com.hls.base.config.mqConfig;
import com.hls.base.utils.mqUtils;
import com.hls.content.dto.AlbumDetailDto;
import com.hls.content.mapper.AlbumMapper;
import com.hls.content.po.*;
import com.hls.content.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>
 * 专辑信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements IAlbumService {


    private mqUtils mqUtils;
    private final ISongService songService;
    private final ISingerService singerService;
    private final IMvService mvService;
    private final ISingerHotService singerHotService;
    private final ITextInfoService textInfoService;
    private final ApplicationContext applicationContext;


    @Override
    public PageResult<Album> pageBySingerId(Long id, PageParam pageParam) {
        Page<Album> page = Page.of(pageParam.getNum(), pageParam.getSize());
        LambdaQueryWrapper<Album> qw = new LambdaQueryWrapper<Album>()
                .eq(Album::getSingerId, id)
                .orderByDesc(Album::getCreateTime);
        Page<Album> res = page(page, qw);

        PageResult<Album> albumPageResult = new PageResult<>();
        albumPageResult.setNum(res.getCurrent());
        albumPageResult.setSize(res.getSize());
        albumPageResult.setTotal(res.getTotal());
        albumPageResult.setItem(res.getRecords());
        return albumPageResult;
    }

    @Override
    public void addAlbum(AlbumDetailDto albumDetailDto) {
        applicationContext.getBean(AlbumServiceImpl.class).add(albumDetailDto);
        mqUtils.addMedia(albumDetailDto.getAvatar());
    }

    @Transactional(rollbackFor = Exception.class)
    protected void add(AlbumDetailDto albumDetailDto) {
        Album album = BeanUtil.copyProperties(albumDetailDto, Album.class);
        if (album.getIntroduction().length() > 50) {
            String str = album.getIntroduction();
            album.setIntroduction(str.substring(0, 25));
            save(album);
            album.setIntroduction(str);
            saveText(album);
        } else {
            save(album);
        }
        Singer byId = singerService.getById(album.getSingerId());
        byId.setAlbumNum(byId.getAlbumNum() + 1);
        singerService.updateById(byId);

        List<Song> songs = albumDetailDto.getSongs();
        List<Song> list = songs.stream()
                .filter(v -> v.getAlbum().isEmpty())
                .filter(v->v.getStatus().equals(Status.pass))
                .peek(song -> song.setAlbum(album.getName()))
                .toList();
        songService.updateBatchById(list);
        long like = 0;
        long play = 0;
        for (Song song : list) {
            like += song.getLikeNum();
            play += song.getPlayNum();
        }
        SingerHot byId1 = singerHotService.getById(album.getSingerId());
        singerHotService.refreshHot(albumDetailDto.getSingerId(),
                byId1.getLikeNum() + like,
                byId1.getPlayNum() + play);
    }


    @Override
    public void deleteAlbum(Long albumId) {
        Album byId = getById(albumId);
        AlbumServiceImpl bean = applicationContext.getBean(AlbumServiceImpl.class);
        bean.del(byId);
        mqUtils.delMedia(byId.getAvatar());
    }

    @Transactional(rollbackFor = Exception.class)
    protected void del(Album byId){
        if (byId == null) {
            return;
        }
        LambdaQueryWrapper<Song> eq = new LambdaQueryWrapper<Song>()
                .eq(Song::getAlbum, byId.getName())
                .eq(Song::getStatus, Status.pass);
        List<Song> list = songService.list(eq);
        if (list != null && !list.isEmpty()) {
            Long playNum = 0L;
            Long likeNum = 0L;
            Long mvNum = 0L;
            for (Song song : list) {
                song.setAlbum(null);
                playNum = song.getPlayNum();
                likeNum = song.getLikeNum();
                Mv byId1 = mvService.getById(song.getId());
                if (byId1 != null) {
                    mvNum++;
                    playNum += byId1.getPlayNum();
                    likeNum += byId1.getLikeNum();
                }
            }
            songService.updateBatchById(list);
            Singer byId1 = singerService.getById(byId.getSingerId());
            byId1.setAlbumNum(byId1.getAlbumNum() - 1);
            byId1.setMvNum((int) (byId1.getMvNum() - mvNum));
            byId1.setSongNum((byId1.getSongNum() - list.size()));
            singerService.updateById(byId1);
            SingerHot byId2 = singerHotService.getById(byId.getSingerId());
            singerHotService.refreshHot(byId.getSingerId(),
                    byId2.getLikeNum() - likeNum,
                    byId2.getPlayNum() - playNum);
        }
        LambdaQueryWrapper<TextInfo> eq1 = new LambdaQueryWrapper<TextInfo>()
                .eq(TextInfo::getAlbumId, byId.getId());
        textInfoService.remove(eq1);
        removeById(byId.getId());
    }



    @Transactional(rollbackFor = Exception.class)
    public void saveText(Album album) {
        TextInfo textInfo = new TextInfo();
        textInfo.setAlbumId(album.getId());
        textInfo.setUserId(album.getUserId());
        textInfo.setContent(album.getIntroduction());
        textInfo.setCreateTime(LocalDateTime.now());
        textInfoService.save(textInfo);
    }


    @Override
    public void updateAlbum(AlbumDetailDto albumDetailDto) {
        Album byId = getById(albumDetailDto.getId());
        if(byId == null){
            return;
        }
        AlbumServiceImpl bean = applicationContext.getBean(AlbumServiceImpl.class);
        if(!byId.getAvatar().equals(albumDetailDto.getAvatar())){
            mqUtils.delMedia(byId.getAvatar());
            mqUtils.addMedia(albumDetailDto.getAvatar());
        }
        bean.del(byId);
        bean.add(albumDetailDto);
    }

    @Override
    public AlbumDetailDto getAlbumDetail(Long albumId) {
        Album album = getById(albumId);
        if (album == null) {
            return null;
        }
        LambdaQueryWrapper<Song> eq = new LambdaQueryWrapper<Song>()
                .eq(Song::getAlbum, album.getName())
                .eq(Song::getStatus, Status.pass);
        List<Song> list = songService.list(eq);
        AlbumDetailDto albumDetailDto = BeanUtil.copyProperties(album, AlbumDetailDto.class);
        albumDetailDto.setSongs(list);
        return albumDetailDto;
    }

}
