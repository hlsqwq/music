package com.hls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.config.MqConfig;
import com.hls.base.utils.AuditState;
import com.hls.base.utils.MqBase;
import com.hls.content.dto.AlbumDetailDto;
import com.hls.content.mapper.AlbumMapper;
import com.hls.content.po.*;
import com.hls.content.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    private final ISongService songService;
    private final ISingerService singerService;
    private final IMvService mvService;
    private final ITextInfoService textInfoService;
    private final ISingerHotService singerHotService;
    private final MqBase mqBase;
    private final ApplicationContext applicationContext;

    /**
     * 获取歌手的专辑
     *
     * @param id        歌手 id
     * @param order     排序字段 hot and createTime
     * @param pageParam 分页信息
     * @return 专辑
     */
    @Override
    public PageResult<Album> pageBySingerId(Long id, String order, PageParam pageParam) {
        Page<Album> page = Page.of(pageParam.getNum(), pageParam.getSize());
        LambdaQueryWrapper<Album> qw = new LambdaQueryWrapper<Album>()
                .eq(Album::getSingerId, id)
                .orderByDesc(Objects.equals(order, "hot") ? Album::getHot : Album::getCreateTime);
        Page<Album> res = page(page, qw);

        PageResult<Album> albumPageResult = new PageResult<>();
        albumPageResult.setNum(pageParam.getNum());
        albumPageResult.setSize(pageParam.getSize());
        albumPageResult.setTotal(res.getTotal());
        albumPageResult.setItem(res.getRecords());
        return albumPageResult;
    }

    @Override
    public void addAlbum(AlbumDetailDto albumDetailDto) {
        applicationContext.getBean(AlbumServiceImpl.class).add(albumDetailDto);

        // todo mq 同步 头像 media
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
        for (int i = 0; i < songs.size(); i++) {
            if (Objects.isNull(songs.get(i)) || songs.get(i).getStatus().equals(AuditState.pass)) {
                songs.get(i).setAlbumId(album.getId());
                songs.get(i).setAlbumName(album.getName());
                songs.get(i).setAlbumOrder(i);
            }
        }
        songService.updateBatchById(songs);
    }

    @Override
    public void deleteAlbum(Long albumId) {
        Album byId = getById(albumId);
        AlbumServiceImpl bean = applicationContext.getBean(AlbumServiceImpl.class);
        bean.del(byId);
        mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY, byId.getAvatar());
    }

    @Transactional(rollbackFor = Exception.class)
    protected void del(Album byId) {
        if (byId == null) {
            return;
        }
        LambdaQueryWrapper<Song> eq = new LambdaQueryWrapper<Song>()
                .eq(Song::getAlbum, byId.getName())
                .eq(Song::getStatus, AuditState.pass);
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
        if (byId == null) {
            return;
        }
        AlbumServiceImpl bean = applicationContext.getBean(AlbumServiceImpl.class);
        if (!byId.getAvatar().equals(albumDetailDto.getAvatar())) {
            mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY, byId.getAvatar());
            // 这里可以添加添加媒体的逻辑，根据实际需求实现
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
                .eq(Song::getStatus, AuditState.pass);
        List<Song> list = songService.list(eq);
        AlbumDetailDto albumDetailDto = BeanUtil.copyProperties(album, AlbumDetailDto.class);
        albumDetailDto.setSongs(list);
        return albumDetailDto;
    }

}
