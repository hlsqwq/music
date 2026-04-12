package com.hls.media.controller;


import com.hls.base.R;
import com.hls.media.service.IMediaService;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class MediaController {

    private final IMediaService mediaService;


    /**
     * 检查文件是否上传
     *
     * @param fileMd5  文件MD5
     * @param fileName 文件名
     * @return 如果存在返回 ok 不存在 签证 其他用户正在上传 busy
     */
    @GetMapping("/check/file")
    public R<String> checkFile(String fileMd5, String fileName) {
        return mediaService.checkFile(fileMd5, fileName);
    }

    /**
     * 检查分块文件是否存在
     *
     * @param id       分块索引
     * @param chunkMd5 分块的MD5
     * @param fileMd5  完整文件的MD5
     * @return 如果存在返回 ok 不存在 签证 其他用户正在上传 busy
     */
    @GetMapping("/check/chunk")
    public R<String> checkChunk(Integer id, String chunkMd5, String fileMd5) {
        return mediaService.checkChunk(id, chunkMd5, fileMd5);
    }


    /**
     * 合并文件
     *
     * @param total    文件分块总数
     * @param fileMd5  文件MD5
     * @param fileName 文件名
     * @return 是否合并成功
     */
    @GetMapping("/merge")
    public R<Object> merge(int total, String fileMd5, String fileName) {
        return mediaService.merge(total, fileMd5, fileName);
    }




}
