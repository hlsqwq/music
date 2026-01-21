package com.hls.media.service.impl;


import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.utils.MinioTempRedis;
import com.hls.media.config.MinioConfig;
import com.hls.media.mapper.MediaMapper;
import com.hls.media.po.Media;
import com.hls.media.po.MediaTemp;
import com.hls.media.service.IMediaService;
import com.hls.media.service.IMediaTempService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 媒体文件信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MediaServiceImpl extends ServiceImpl<MediaMapper, Media> implements IMediaService {


    private final MinioTempRedis minioTempRedis;
    private final MinioConfig minioConfig;
    private final MinioClient minioClient;
    private final IMediaTempService mediaTempService;


    /**
     * 获取基础路径
     * @param md5
     * @return
     */
    private String getBasePath(String md5) {
        return md5.charAt(0) + "/" + md5.substring(1) + "/" + md5.substring(2)+"/";
    }

    /**
     * 获取分块的路径
     * @param id
     * @param md5
     * @return
     */
    private String getChunkPath(Long id,String md5) {
        String path = getBasePath(md5);
        return path+"chunk"+"/"+id;
    }

    /**
     * 获取文件路径
     * @param md5
     * @param fileName
     * @return
     */
    private String getFilePath(String md5,String fileName) {
        String path = getBasePath(md5);
        return path+fileName;
    }

    /**
     * 获取文件状态
     * @param id    分块索引，文件传null
     * @param md5   文件md5
     * @param fileName  文件名，分块传null
     * @return
     */
    private StatObjectResponse getStat(Long id,String md5,String fileName) {
        String path ="";
        if(fileName!=null){
            path = getFilePath(md5,fileName);
        }else{
            path = getChunkPath(id,md5);
        }
        StatObjectArgs build = StatObjectArgs.builder()
                .bucket(minioConfig.temp)
                .object(path)
                .build();
        try {
            return minioClient.statObject(build);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *  获取上传签名
     * @param id    分块索引，文件传null
     * @param md5   文件md5
     * @param fileName  文件名，分块传null
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    private String getSignature(Long id,String md5,String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String path ="";
        if(fileName!=null){
            path = getFilePath(md5,fileName);
        }else{
            path = getChunkPath(id,md5);
        }
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(minioConfig.temp)
                .object(path)
                .expiry(10, TimeUnit.MINUTES)
                .method(Method.PUT)
                .build());
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public String checkFile(Long userId, String fileMd5, String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String filePath = getFilePath(fileMd5, fileName);
        if (minioTempRedis.checkFile(fileMd5)) {
            return "ok";
        }
        StatObjectResponse stat = getStat(0L,fileMd5, fileName);
        if(stat==null){
            return getSignature(null,fileMd5,fileName);
        }
        MediaTemp mediaTemp = new MediaTemp();
        mediaTemp.setPath(filePath);
        mediaTemp.setUrl(minioConfig.temp+"/"+filePath);
        mediaTemp.setSize(stat.size());
        mediaTemp.setCreateTime(LocalDateTime.now());
        mediaTemp.setUserId(userId);
        mediaTempService.save(mediaTemp);

        String etag = stat.etag();
        if(!etag.equals(fileMd5)){
            return getSignature(null,fileMd5,fileName);
        }
        minioTempRedis.addFile(fileMd5);
        return "ok";
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String checkChunk(Long userId, Long id, String chunkMd5, String fileMd5) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String filePath = getChunkPath(id,chunkMd5);
        if (minioTempRedis.checkFile(fileMd5)) {
            return "ok";
        }
        StatObjectResponse stat = getStat(id,fileMd5, null);
        if(stat==null){
            return getSignature(id,fileMd5,null);
        }
        MediaTemp mediaTemp = new MediaTemp();
        mediaTemp.setPath(filePath);
        mediaTemp.setUrl(minioConfig.temp+"/"+filePath);
        mediaTemp.setSize(stat.size());
        mediaTemp.setCreateTime(LocalDateTime.now());
        mediaTemp.setUserId(userId);
        mediaTempService.save(mediaTemp);

        String etag = stat.etag();
        if(!etag.equals(fileMd5)){
            return getSignature(id,fileMd5,null);
        }
        minioTempRedis.addFile(fileMd5);
        return "ok";
    }








}
