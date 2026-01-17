package com.hls.content.service.impl;

import com.hls.content.po.Media;
import com.hls.content.mapper.MediaMapper;
import com.hls.content.service.IMediaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 媒体文件信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class MediaServiceImpl extends ServiceImpl<MediaMapper, Media> implements IMediaService {

}
