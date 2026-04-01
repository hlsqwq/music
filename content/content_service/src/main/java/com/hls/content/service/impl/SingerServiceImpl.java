package com.hls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.config.MqConfig;
import com.hls.base.exception.MusicException;
import com.hls.base.utils.MqBase;
import com.hls.content.dto.EditSingerDto;
import com.hls.content.dto.SingerDto;
import com.hls.content.mapper.SingerMapper;
import com.hls.content.po.Singer;
import com.hls.content.po.TextInfo;
import com.hls.content.service.ISingerService;
import com.hls.content.service.ISongService;
import com.hls.content.service.ITextInfoService;
import com.hls.content.utils.RedisHotUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
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

    private final ISongService songService;
    private final ITextInfoService textInfoService;
    private final RedisHotUtil redisHotUtil;
    private final MqBase mqBase;
    private final ApplicationContext applicationContext;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add_singer(SingerDto singerDto) {
        if (singerDto == null) {
            MusicException.cast("对象不可为空");
            return;
        }
        String introduction = singerDto.getIntroduction();
        singerDto.setIntroduction(introduction.substring(0, 50));
        String substring = introduction.substring(50);
        if(!substring.isBlank()){
            TextInfo textInfo = new TextInfo();
            textInfo.setContent(substring);
            textInfoService.save(textInfo);
        }
        Singer singer = BeanUtil.copyProperties(singerDto, Singer.class);
        save(singer);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update_singer(EditSingerDto editSingerDto) {
        SingerServiceImpl bean = applicationContext.getBean(SingerServiceImpl.class);
        bean.del_singer(editSingerDto.getId());
        bean.add_singer(editSingerDto);
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
                .eq(TextInfo::getId, byId.getIntroductionId());
        textInfoService.remove(qw);
        String substring = byId.getAvatarUrl().substring(byId.getAvatarUrl().indexOf("/") + 1);
        substring = substring.substring(substring.indexOf("/") + 1);
        mqBase.sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY,
                new com.hls.base.dto.DelTempMedia(null,"music",substring));
        removeById(id);
    }


}
