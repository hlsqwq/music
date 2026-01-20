package com.hls.content.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.utils.WorksCateTopN;
import com.hls.content.dto.EditSingerDto;
import com.hls.content.dto.SingerDto;
import com.hls.content.mapper.SingerMapper;
import com.hls.content.po.Singer;
import com.hls.content.service.ISingerService;
import com.hls.content.service.ISongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 歌手信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@Service
public class SingerServiceImpl extends ServiceImpl<SingerMapper, Singer> implements ISingerService {


    private final WorksCateTopN worksCateTopN;
    private final ISongService songService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add_singer(Long userId, SingerDto singerDto) {
        //todo


        Singer singer = BeanUtil.copyProperties(singerDto, Singer.class);
        singer.setCreateTime(LocalDateTime.now());
        save(singer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update_singer(Long userId, EditSingerDto editSingerDto) {
        //todo


        Singer byId = getById(editSingerDto.getId());
        BeanUtil.copyProperties(editSingerDto, byId, CopyOptions.create().setIgnoreNullValue(true));
        updateById(byId);
    }

    @Override
    public List<EditSingerDto> getTop10(int id) {
        Set set = worksCateTopN.getTopN("category_" + id + "_singer", 10);
        ArrayList<Integer> arr = new ArrayList<>();
        for (Object o : set) {
            arr.add((Integer) o);
        }
        LambdaQueryWrapper<Singer> in = new LambdaQueryWrapper<Singer>().in(Singer::getId, arr);
        List<Singer> singers = list(in);
        return singers.stream()
                .map(v -> BeanUtil.copyProperties(v, EditSingerDto.class))
                .toList();
    }


}
