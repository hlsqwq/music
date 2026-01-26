package com.hls.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.Status;
import com.hls.base.utils.mqUtils;
import com.hls.content.po.Album;
import com.hls.content.po.Mv;
import com.hls.content.po.Singer;
import com.hls.content.po.SingerHot;
import com.hls.content.po.TextInfo;
import com.hls.content.mapper.MvMapper;
import com.hls.content.mapper.SingerHotMapper;
import com.hls.content.mapper.SingerMapper;
import com.hls.content.mapper.TextInfoMapper;
import com.hls.content.service.IMvService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * <p>
 * 歌手mv 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-20
 */
@RequiredArgsConstructor
@Service
public class MvServiceImpl extends ServiceImpl<MvMapper, Mv> implements IMvService {

    private final SingerMapper singerMapper;
    private final TextInfoMapper textInfoMapper;
    private final mqUtils mqUtils;
    private final SingerHotMapper singerHotMapper;

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



    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addMv(Mv mv) {
        save(mv);
        Singer singer = singerMapper.selectById(mv.getSingerId());
        if (singer != null) {
            singer.setMvNum(singer.getMvNum() + 1);
            singerMapper.updateById(singer);
        }
        if (mv.getAvatar() != null) {
            mqUtils.addMedia(mv.getAvatar());
        }
        if (mv.getVideo() != null) {
            mqUtils.addMedia(mv.getVideo());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteMv(Integer mvId) {
        Mv mv = getById(mvId);
        if (mv != null) {
            if (mv.getAvatar() != null) {
                mqUtils.delMedia(mv.getAvatar());
            }
            if (mv.getVideo() != null) {
                mqUtils.delMedia(mv.getVideo());
            }
            removeById(mvId);

            Singer singer = singerMapper.selectById(mv.getSingerId());
            if (singer != null && singer.getMvNum() > 0) {
                singer.setMvNum(singer.getMvNum() - 1);
                singerMapper.updateById(singer);
            }

            SingerHot singerHot = singerHotMapper.selectById(mv.getSingerId());
            if (singerHot != null && mv.getPlayNum() != null) {
                singerHot.setPlayNum(singerHot.getPlayNum() - mv.getPlayNum());
                singerHotMapper.updateById(singerHot);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMv(Mv mv) {
        Mv byId = getById(mv.getId());

        if (byId.getAvatar() != null && !byId.getAvatar().equals(mv.getAvatar())) {
            mqUtils.delMedia(byId.getAvatar());
        }
        if (byId.getVideo() != null && !byId.getVideo().equals(mv.getVideo())) {
            mqUtils.delMedia(byId.getVideo());
        }

        updateById(mv);

        if (mv.getAvatar() != null && !mv.getAvatar().equals(byId.getAvatar())) {
            mqUtils.addMedia(mv.getAvatar());
        }
        if (mv.getVideo() != null && !mv.getVideo().equals(byId.getVideo())) {
            mqUtils.addMedia(mv.getVideo());
        }

        if (mv.getPlayNum() != null && byId.getPlayNum() != null) {
            SingerHot singerHot = singerHotMapper.selectById(mv.getSingerId());
            if (singerHot != null) {
                long playNumDiff = mv.getPlayNum() - byId.getPlayNum();
                singerHot.setPlayNum(singerHot.getPlayNum() + playNumDiff);
                singerHotMapper.updateById(singerHot);
            }
        }
    }

    @Override
    public Mv getMvDetail(Integer mvId) {
        return getById(mvId);
    }

    @Override
    public List<Mv> getMvList() {
        LambdaQueryWrapper<Mv> qw = new LambdaQueryWrapper<Mv>()
                .eq(Mv::getStatus, Status.pass)
                .orderByDesc(Mv::getCreateTime);
        return list(qw);
    }
}
