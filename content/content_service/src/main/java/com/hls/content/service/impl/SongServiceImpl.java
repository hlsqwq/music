package com.hls.content.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.Status;
import com.hls.content.mapper.SongMapper;
import com.hls.content.po.Song;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.service.ISongService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 歌曲信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements ISongService {



    @Override
    public PageResult<Song> pageBySingerId(Long id, PageParam pageParam) {
        Page<Song> page = Page.of(pageParam.getNum(), pageParam.getSize());
        LambdaQueryWrapper<Song> qw = new LambdaQueryWrapper<Song>()
                .eq(Song::getSingerId, id)
                .eq(Song::getStatus,Status.pass)
                .orderByDesc(Song::getLikeNum);
        Page<Song> res = page(page, qw);
        PageResult<Song> songPageResult = new PageResult<>();
        songPageResult.setTotal(res.getTotal());
        songPageResult.setItem(res.getRecords());
        songPageResult.setNum(res.getCurrent());
        songPageResult.setSize(res.getSize());
        return songPageResult;
    }
}
