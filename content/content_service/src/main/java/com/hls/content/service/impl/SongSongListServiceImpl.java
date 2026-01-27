package com.hls.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hls.content.mapper.SongSongListMapper;
import com.hls.content.po.SongSongList;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.service.ISongSongListService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 歌曲-歌单关联表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class SongSongListServiceImpl extends ServiceImpl<SongSongListMapper, SongSongList> implements ISongSongListService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addSongToSongList(Integer songId, Integer songListId) {
        // 检查是否已存在关联
        if (!existsRelation(songId, songListId)) {
            SongSongList songSongList = new SongSongList();
            songSongList.setSongId(songId);
            songSongList.setSongListId(songListId);
            save(songSongList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchAddSongsToSongList(List<Integer> songIds, Integer songListId) {
        List<SongSongList> songSongLists = new ArrayList<>();
        for (Integer songId : songIds) {
            if (!existsRelation(songId, songListId)) {
                SongSongList songSongList = new SongSongList();
                songSongList.setSongId(songId);
                songSongList.setSongListId(songListId);
                songSongLists.add(songSongList);
            }
        }
        if (!songSongLists.isEmpty()) {
            saveBatch(songSongLists);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeSongFromSongList(Integer songId, Integer songListId) {
        remove(
                new LambdaQueryWrapper<SongSongList>()
                        .eq(SongSongList::getSongId, songId)
                        .eq(SongSongList::getSongListId, songListId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchRemoveSongsFromSongList(List<Integer> songIds, Integer songListId) {
        remove(
                new LambdaQueryWrapper<SongSongList>()
                        .in(SongSongList::getSongId, songIds)
                        .eq(SongSongList::getSongListId, songListId)
        );
    }

    @Override
    public List<Integer> getSongIdsBySongListId(Integer songListId) {
        List<SongSongList> songSongLists = list(
                new LambdaQueryWrapper<SongSongList>()
                        .eq(SongSongList::getSongListId, songListId)
        );
        List<Integer> songIds = new ArrayList<>();
        for (SongSongList songSongList : songSongLists) {
            songIds.add(songSongList.getSongId());
        }
        return songIds;
    }

    /**
     * 检查歌曲和歌单之间是否已存在关联
     * @param songId 歌曲ID
     * @param songListId 歌单ID
     * @return 是否存在关联
     */
    private boolean existsRelation(Integer songId, Integer songListId) {
        return count(
                new LambdaQueryWrapper<SongSongList>()
                        .eq(SongSongList::getSongId, songId)
                        .eq(SongSongList::getSongListId, songListId)
        ) > 0;
    }

}
