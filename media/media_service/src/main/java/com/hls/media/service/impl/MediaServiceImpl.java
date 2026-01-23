package com.hls.media.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.utils.MinioTempRedis;
import com.hls.media.config.MinioConfig;
import com.hls.media.mapper.MediaMapper;
import com.hls.media.po.Media;
import com.hls.media.po.MediaTemp;
import com.hls.media.service.IMediaService;
import com.hls.media.service.IMediaTempService;
import groovy.lang.Lazy;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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


    private final ApplicationContext applicationContext;


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
            log.error("md5:{},fileName:{}，index：{}，分块合并失败{}",md5,fileName,id, e.getMessage());
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

        MediaServiceImpl bean = applicationContext.getBean(MediaServiceImpl.class);
        bean.saveTempDb(userId,getFilePath(fileMd5,filePath),stat);

        String etag = stat.etag();
        if(!etag.equals(fileMd5)){
            return getSignature(null,fileMd5,fileName);
        }
        minioTempRedis.addFile(fileMd5);
        return "ok";
    }

    /**
     * 保存分块数据到Db
     * @param userId
     * @param filePath
     * @param stat
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveTempDb(Long userId,String filePath,StatObjectResponse stat){
        MediaTemp mediaTemp = new MediaTemp();
        mediaTemp.setPath(filePath);
        mediaTemp.setUrl(minioConfig.temp+"/"+filePath);
        mediaTemp.setSize(stat.size());
        mediaTemp.setCreateTime(LocalDateTime.now());
        mediaTemp.setUserId(userId);
        mediaTempService.save(mediaTemp);
    }



    @Override
    public String checkChunk(Long userId, Long id, String chunkMd5, String fileMd5) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String filePath = getChunkPath(id,fileMd5);
        if (minioTempRedis.checkFile(chunkMd5)) {
            return "ok";
        }
        StatObjectResponse stat = getStat(id,fileMd5, null);
        if(stat==null){
            return getSignature(id,fileMd5,null);
        }
        MediaServiceImpl bean = applicationContext.getBean(MediaServiceImpl.class);
        bean.saveTempDb(userId,getChunkPath(id,fileMd5),stat);

        String etag = stat.etag();
        if(!etag.equals(chunkMd5)){
            return getSignature(id,fileMd5,null);
        }
        minioTempRedis.addFile(chunkMd5);
        return "ok";
    }

    /**
     * 检查分块的数量
     * @param total 总数
     * @param fileMd5
     * @return
     */
    private boolean checkChunkNum(int total,String fileMd5){
        for (int i = 0; i < total; i++) {
            StatObjectResponse stat = getStat(Long.valueOf(i), fileMd5, null);
            if(stat==null){
                return false;
            }
        }
        return true;
    }

    @Override
    public String merge(Long userId, int total, String fileMd5, String fileName) {
        if (!checkChunkNum(total,fileMd5)) {
            log.error("分块数量缺失");
            return "分块数量缺失";
        }
        ArrayList<ComposeSource> list = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            ComposeSource build = ComposeSource.builder()
                    .bucket(minioConfig.temp)
                    .object(getChunkPath(Long.valueOf(i), fileMd5))
                    .build();
            list.add(build);
        }
        try {
            minioClient.composeObject(ComposeObjectArgs.builder()
                    .sources(list)
                    .bucket(minioConfig.temp)
                    .object(getFilePath(fileMd5, fileName))
                    .build());
        } catch (Exception e) {
            //todo throw
            e.printStackTrace();
            log.error("md5:{},fileName:{}分块合并失败{}",fileMd5,fileName, e.getMessage());
            return null;
        }
        StatObjectResponse stat = getStat(0L, fileMd5, fileName);
        if(stat==null){
            return null;
        }
        MediaServiceImpl bean = applicationContext.getBean(MediaServiceImpl.class);
        bean.saveTempDb(userId,getFilePath(fileMd5,getFilePath(fileMd5,fileName)),stat);

        if(!stat.etag().equals(fileMd5)){
            log.error("md5:{},fileName:{}合并文件md5错误",fileMd5,fileName);
            return "合并文件md5错误";
        }
        return "ok";
    }


}
