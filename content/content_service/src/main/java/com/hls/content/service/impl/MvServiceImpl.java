package com.hls.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.Status;
import com.hls.content.po.Album;
import com.hls.content.po.Mv;
import com.hls.content.mapper.MvMapper;
import com.hls.content.service.IMvService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌手mv 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-20
 */
@Service
public class MvServiceImpl extends ServiceImpl<MvMapper, Mv> implements IMvService {

    @Override
    public PageResult<Mv> pageBySinger(Long id, PageParam pageParam) {
        Page<Mv> page = Page.of(pageParam.getNum(), pageParam.getSize());
        LambdaQueryWrapper<Mv> qw = new LambdaQueryWrapper<Mv>()
                .eq(Mv::getSingerId, id)
                .eq(Mv::getStatus, Status.pass)
                .orderByDesc(Mv::getPlayNum);
        Page<Mv> res = page(page, qw);

        PageResult<Mv> albumPageResult = new PageResult<>();
        albumPageResult.setNum(res.getCurrent());
        albumPageResult.setSize(res.getSize());
        albumPageResult.setTotal(res.getTotal());
        albumPageResult.setItem(res.getRecords());
        return albumPageResult;
    }
}
