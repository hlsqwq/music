package com.hls.content.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.mapper.TextInfoMapper;
import com.hls.content.po.TextInfo;
import com.hls.content.service.ITextInfoService;
import com.hls.content.vo.TextInfoVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 文本信息表（歌词/歌手/专辑简介） 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class TextInfoServiceImpl extends ServiceImpl<TextInfoMapper, TextInfo> implements ITextInfoService {


    @Transactional(rollbackFor = Exception.class)
    @Override
    public TextInfoVo saveContent(String introduction, Integer sub) {
        TextInfo textInfo = new TextInfo();
        if (introduction != null && !introduction.isBlank() && introduction.length() > sub) {
            textInfo.setContent(introduction);
            save(textInfo);
            introduction = introduction.substring(sub);
        }
        return new TextInfoVo(textInfo.getId(), introduction);
    }


}
