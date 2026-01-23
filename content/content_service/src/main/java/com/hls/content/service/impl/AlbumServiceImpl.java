package com.hls.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.content.mapper.AlbumMapper;
import com.hls.content.po.Album;
import com.hls.content.service.IAlbumService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 专辑信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements IAlbumService {


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
}
