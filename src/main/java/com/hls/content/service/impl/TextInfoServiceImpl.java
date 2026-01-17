package com.hls.content.service.impl;

import com.hls.content.po.TextInfo;
import com.hls.content.mapper.TextInfoMapper;
import com.hls.content.service.ITextInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
