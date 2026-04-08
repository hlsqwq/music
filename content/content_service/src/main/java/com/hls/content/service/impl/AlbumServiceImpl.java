package com.hls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.config.MqConfig;
import com.hls.base.dto.DelTempMedia;
import com.hls.base.po.Album;
import com.hls.base.po.Singer;
import com.hls.base.po.Song;
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
    private final ITextInfoService textInfoService;
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
    public PageResult<Album> pageBySingerId(Integer id, String order, PageParam pageParam) {
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
    }

    @Transactional(rollbackFor = Exception.class)
    protected void add(AlbumDetailDto albumDetailDto) {
        Album album = BeanUtil.copyProperties(albumDetailDto, Album.class);
        if (album.getIntroduction().length() > 50) {
            String str = album.getIntroduction();
            album.setIntroduction(str.substring(0, 50));
            str=str.substring(50);
            if(!str.isBlank()){
                TextInfo textInfo = new TextInfo();
                textInfo.setContent(str);
                textInfoService.save(textInfo);
            }
            save(album);
        } else {
            save(album);
        }
        Singer byId = singerService.getById(album.getSingerId());
        byId.setAlbumNum(byId.getAlbumNum() + 1);
        singerService.updateById(byId);

        List<Song> songs = albumDetailDto.getSongs();
        for (int i = 0; i < songs.size(); i++) {
            if (Objects.isNull(songs.get(i).getAlbumId()) ||
                    songs.get(i).getStatus().equals(AuditState.pass)) {
                songs.get(i).setAlbumId(album.getId());
                songs.get(i).setAlbumName(album.getName());
                songs.get(i).setAlbumOrder(i);
            }
        }
        songService.updateBatchById(songs);
    }

    @Override
    public void deleteAlbum(Integer albumId) {
        Album byId = getById(albumId);
        applicationContext.getBean(AlbumServiceImpl.class).del(byId);
        //        http://192.168.124.8:9000/music/a3.png
        String substring = byId.getAvatarUrl().substring(byId.getAvatarUrl().indexOf("/") + 1);
        substring = substring.substring(substring.indexOf("/") + 1);
        mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY,
                new DelTempMedia(byId.getAvatarId(),null,"music",substring));
    }

    @Transactional(rollbackFor = Exception.class)
    protected void del(Album byId) {
        if (byId == null) {
            return;
        }
        LambdaQueryWrapper<Song> eq = new LambdaQueryWrapper<Song>()
                .eq(Song::getAlbumId, byId.getId())
                .eq(Song::getStatus, AuditState.pass);
        List<Song> list = songService.list(eq);
        if (list != null && !list.isEmpty()) {
            for (Song song : list) {
                song.setAlbumId(null);
                song.setAlbumOrder(null);
                song.setAlbumName(null);
            }
            songService.updateBatchById(list);
            Singer byId1 = singerService.getById(byId.getSingerId());
            byId1.setAlbumNum(byId1.getAlbumNum() - 1);
            singerService.updateById(byId1);
        }
        if(byId.getIntroductionId()!=null){
            textInfoService.removeById(byId.getIntroductionId());
        }
    }


    @Override
    public void updateAlbum(AlbumDetailDto albumDetailDto) {
        Album byId = getById(albumDetailDto.getId());
        if (byId == null) {
            return;
        }
        AlbumServiceImpl bean = applicationContext.getBean(AlbumServiceImpl.class);
        bean.deleteAlbum(albumDetailDto.getId());
        bean.addAlbum(albumDetailDto);
    }

    @Override
    public AlbumDetailDto getAlbumDetail(Integer albumId) {
        Album album = getById(albumId);
        if (album == null) {
            return null;
        }
        LambdaQueryWrapper<Song> eq = new LambdaQueryWrapper<Song>()
                .eq(Song::getAlbumId, album.getId())
                .eq(Song::getStatus, AuditState.pass);
        List<Song> list = songService.list(eq);
        AlbumDetailDto albumDetailDto = BeanUtil.copyProperties(album, AlbumDetailDto.class);
        albumDetailDto.setSongs(list);
        return albumDetailDto;
    }

}
