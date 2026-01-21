package com.hls.media.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.media.po.Media;
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

    String checkFile(Long userId, String fileMd5, String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    String checkChunk(Long userId, Long id, String chunkMd5, String fileMd5) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
