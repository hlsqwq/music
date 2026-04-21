package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.content.po.TextInfo;
import com.hls.content.vo.TextInfoVo;

/**
 * <p>
 * 文本信息表（歌词/歌手/专辑简介） 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
public interface ITextInfoService extends IService<TextInfo> {

    /**
     * 保存长文本
     * @param text 文本内容
     * @param sub 截取的长度
     * @return
     */
    TextInfoVo saveContent(String text, Integer sub);
}
