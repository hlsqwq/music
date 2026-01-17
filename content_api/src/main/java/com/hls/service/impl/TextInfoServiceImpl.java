package com.hls.service.impl;

import com.hls.po.TextInfo;
import com.hls.mapper.TextInfoMapper;
import com.hls.service.ITextInfoService;
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
