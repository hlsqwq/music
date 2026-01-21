package com.hls.media.controller;


import com.hls.media.service.IMediaService;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * 媒体文件信息表 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/media")
public class MediaController {

    private final IMediaService mediaService;


    /**
     * 检查文件是否上传
     * @param fileMd5
     * @param fileName
     * @return 上传返回，ok，未上传，签证
     */
    @GetMapping("/check/file")
    public String checkFile(String fileMd5,String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //todo
        Long userId=1L;
        return mediaService.checkFile(userId,fileMd5,fileName);
    }

    /**
     * 分块检查
     * @param id    分块索引
     * @param chunkMd5  分块md5
     * @param fileMd5   文件md5
     * @return  上传返回，ok，未上传，签证
     */
    @GetMapping("/check/chunk")
    public String checkChunk(Long id,String chunkMd5,String fileMd5) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //todo
        Long userId=1L;
        return mediaService.checkChunk(userId,id,chunkMd5,fileMd5);
    }



//    @GetMapping("/merge")
//    public


}
