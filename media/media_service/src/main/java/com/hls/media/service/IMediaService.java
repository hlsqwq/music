package com.hls.media.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.R;
import com.hls.media.po.Media;
import com.hls.media.vo.MediaVo;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * 媒体文件信息表 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
public interface IMediaService extends IService<Media> {


    /**
     * 检查文件是否上传
     *
     * @param fileMd5  文件MD5
     * @param fileName 文件名
     * @return 上传返回，ok，未上传，签证
     */
    R<MediaVo> checkFile(String fileMd5, String fileName);

    /**
     * 检查分块文件是否存在
     *
     * @param id       分块索引
     * @param chunkMd5 分块的MD5
     * @param fileMd5  完整文件的MD5
     * @return 如果存在返回 ok 不存在 签证 其他用户正在上传 busy
     */
    R<String> checkChunk(Integer id, String chunkMd5, String fileMd5);

    R<MediaVo> merge(int total, String fileMd5, String fileName);
}
