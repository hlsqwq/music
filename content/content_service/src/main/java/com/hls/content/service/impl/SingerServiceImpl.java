package com.hls.content.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.exception.MusicException;
import com.hls.base.utils.WorksCateTopN;
import com.hls.base.utils.mqUtils;
import com.hls.content.dto.EditSingerDto;
import com.hls.content.dto.SingerDto;
import com.hls.content.mapper.SingerMapper;
import com.hls.content.po.Singer;
import com.hls.content.po.SingerHot;
import com.hls.content.po.TextInfo;
import com.hls.content.service.ISingerHotService;
import com.hls.content.service.ISingerService;
import com.hls.content.service.ISongService;
import com.hls.content.service.ITextInfoService;
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
    private final ITextInfoService textInfoService;
    private final ISingerHotService singerHotService;
    private final mqUtils mqUtils;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add_singer(SingerDto singerDto) {
        if(singerDto==null){
            MusicException.cast("对象不可为空");
            return;
        }
        String introduction = singerDto.getIntroduction();
        if(introduction!=null && introduction.length()>50){
            singerDto.setIntroduction(introduction.substring(0, 50));
        }
        Singer singer = BeanUtil.copyProperties(singerDto, Singer.class);
        singer.setCreateTime(LocalDateTime.now());
        save(singer);

        saveText(singer.getId(),introduction);
        SingerHot singerHot = new SingerHot();
        singerHot.setId(singer.getId());
        singerHotService.save(singerHot);
        mqUtils.addMedia(singerDto.getAvatar());
    }

    @Transactional(rollbackFor = Exception.class)
    protected void saveText(Integer id, String introduction) {
        TextInfo textInfo = new TextInfo();
        textInfo.setSingerId(id);
        textInfo.setContent(introduction);
        //todo
//        textInfo.setUserId()
        textInfo.setCreateTime(LocalDateTime.now());
        textInfoService.save(textInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update_singer(EditSingerDto editSingerDto) {
        Singer byId = getById(editSingerDto.getId());
        if(byId.getAvatar()!=null && byId.getAvatar().equals(editSingerDto.getAvatar())){
            mqUtils.delMedia(byId.getAvatar());
            mqUtils.addMedia(editSingerDto.getAvatar());
        }
        if(editSingerDto.getIntroduction()!=null &&
                !editSingerDto.getIntroduction().equals(byId.getIntroduction()) ){
            LambdaQueryWrapper<TextInfo> qw = new LambdaQueryWrapper<TextInfo>()
                    .eq(TextInfo::getSingerId, byId.getId());
            textInfoService.remove(qw);
            if(editSingerDto.getIntroduction().length()>50){
                saveText(editSingerDto.getId(),editSingerDto.getIntroduction());
            }
        }
        BeanUtil.copyProperties(editSingerDto, byId, CopyOptions.create().setIgnoreNullValue(true));
        byId.setIntroduction(editSingerDto.getIntroduction().substring(0, 50));
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void del_singer(Integer id) {
        Singer byId = getById(id);
        LambdaQueryWrapper<TextInfo> qw = new LambdaQueryWrapper<TextInfo>()
                .eq(TextInfo::getSingerId, byId.getId());
        textInfoService.remove(qw);
        if(byId.getAvatar()!=null){
            mqUtils.delMedia(byId.getAvatar());
        }
        removeById(id);
    }


}
