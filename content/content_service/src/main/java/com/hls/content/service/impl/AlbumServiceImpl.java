package com.hls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.Status;
import com.hls.content.dto.AlbumDetailDto;
import com.hls.content.mapper.AlbumMapper;
import com.hls.content.po.*;
import com.hls.content.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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

    private final RabbitTemplate rabbitTemplate;
    private final ISongService songService;
    private final ISingerService singerService;
    private final IMvService mvService;
    private final ISingerHotService singerHotService;
    private final ITextInfoService textInfoService;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addAlbum(Album album) {
        save(album);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteAlbum(Long albumId) {
        Album byId = getById(albumId);
        if(byId == null){
            return;
        }
        LambdaQueryWrapper<Song> eq = new LambdaQueryWrapper<Song>()
                .eq(Song::getAlbum, byId.getName())
                .eq(Song::getStatus, Status.pass);
        List<Song> list = songService.list(eq);
        if(list!=null&& !list.isEmpty()){
            Long playNum= 0L;
            Long likeNum= 0L;
            Long mvNum= 0L;
            for (Song song : list) {
                song.setAlbum(null);
                playNum = song.getPlayNum();
                likeNum = song.getLikeNum();
                Mv byId1 = mvService.getById(song.getId());
                if(byId1!=null){
                    mvNum++;
                    playNum += byId1.getPlayNum();
                    likeNum += byId1.getLikeNum();
                }
            }
            songService.updateBatchById(list);
            Singer byId1 = singerService.getById(byId.getSingerId());
            byId1.setAlbumNum(byId1.getAlbumNum()-1);
            byId1.setMvNum(byId1.getMvNum()-mvNum);
            byId1.setSongNum((byId1.getSongNum()-list.size()));
            singerService.updateById(byId1);
            SingerHot byId2 = singerHotService.getById(byId.getSingerId());
            singerHotService.refreshHot(byId.getSingerId(),byId2.getLikeNum()-likeNum,byId2.getPlayNum()-playNum);
        }
        LambdaQueryWrapper<TextInfo> eq1 = new LambdaQueryWrapper<TextInfo>()
                .eq(TextInfo::getAlbumId, albumId);
        textInfoService.remove(eq1);
        //todo

        removeById(albumId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAlbum(Album album) {
        updateById(album);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAlbumSongs(Long albumId, List<Long> songIds) {
        Album byId = getById(albumId);
        if (byId == null) {
            return;
        }
        List<Song> songs = songService.listByIds(songIds);
        List<Song> list = songs.stream()
                .filter(v -> Objects.equals(v.getStatus(), Status.pass))
                .peek(v -> v.setAlbum(byId.getName()))
                .toList();
        songService.updateBatchById(list);
    }
}
