package com.hls.media.service.impl;


import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.R;
import com.hls.base.config.MqConfig;
import com.hls.base.config.UserContext;
import com.hls.base.utils.*;
import com.hls.media.config.MinioConfig;
import com.hls.base.dto.DelTempMedia;
import com.hls.media.mapper.MediaMapper;
import com.hls.media.po.Media;
import com.hls.media.service.IMediaService;
import com.hls.media.service.IUserMediaService;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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


    private final MinioConfig minioConfig;
    private final MinioClient minioClient;

    private final RedissonClient redissonClient;
    private final RedisKeys redisKeys;
    private final RedisBase redisBase;
    private final MqBase mqBase;
    private final IUserMediaService userMediaService;
    private final ApplicationContext applicationContext;


    private final static List<String> audio = List.of("mp3");
    private final static List<String> video = List.of("mp4", "avi");
    private final static List<String> pic = List.of("jpg", "png");


    /**
     * 获取基础路径
     *
     * @param md5 文件MD5值
     * @return 路径前缀
     */
    private String getBasePath(String md5) {
        return md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5.substring(2) + "/";
    }

    /**
     * 获取分块的路径
     *
     * @param id  分块 id
     * @param md5 完整文件MD5
     * @return 分块路径
     */
    private String getChunkPath(Integer id, String md5) {
        String path = getBasePath(md5);
        return path + "chunk" + "/" + id;
    }

    /**
     * 获取文件路径
     *
     * @param md5      文件MD5
     * @param fileName 文件名
     * @return 文件路径
     */
    private String getFilePath(String md5, String fileName) {
        String path = getBasePath(md5);
        return path + fileName;
    }

    /**
     * 获取文件状态
     *
     * @param id       分块索引，文件传null
     * @param md5      文件md5
     * @param fileName 文件名，分块传null
     * @return 文件状态
     */
    private StatObjectResponse getStat(Integer id, String md5, String fileName) {
        String path = "";
        if (fileName != null) {
            path = getFilePath(md5, fileName);
        } else {
            path = getChunkPath(id, md5);
        }
        StatObjectArgs build = StatObjectArgs.builder()
                .bucket(minioConfig.music)
                .object(path)
                .build();
        try {
            return minioClient.statObject(build);
        } catch (Exception e) {
            log.error("md5:{},filePath:{}，获取文件状态失败,{}", md5, path, e.getMessage());
            return null;
        }
    }

    /**
     * 获取文件状态
     *
     * @param md5      文件md5
     * @param fileName 文件名
     * @return 文件状态
     */
    private StatObjectResponse getStatFile(String md5, String fileName) {
        return getStat(null, md5, fileName);
    }


    /**
     * 获取分块状态
     *
     * @param id  分块索引
     * @param md5 完整文件MD5
     * @return 文件状态
     */
    private StatObjectResponse getStatChunk(Integer id, String md5) {
        return getStat(id, md5, null);
    }

    /**
     * 获取上传签证
     *
     * @param id       分块索引，文件传null
     * @param md5      文件md5
     * @param fileName 文件名，分块传null
     * @return 签证
     */
    private String getSignature(Integer id, String md5, String fileName) {
        String path = "";
        if (fileName != null) {
            path = getFilePath(md5, fileName);
        } else {
            path = getChunkPath(id, md5);
        }
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConfig.music)
                    .object(path)
                    .expiry(1, TimeUnit.MINUTES)
                    .method(Method.PUT)
                    .build());
        } catch (Exception e) {
            log.error("获取签证失败：md5：{}，path：{},{}", md5, path, e.getMessage());
            return null;
        }
    }


    /**
     * 获取文件上传签证
     *
     * @param md5      文件md5
     * @param fileName 文件名
     * @return 签证
     */
    private String getSignatureFile(String md5, String fileName) {
        return getSignature(null, md5, fileName);
    }

    /**
     * 获取分块上传签证
     *
     * @param id  分块索引
     * @param md5 完整文件MD5
     * @return 签证
     */
    private String getSignatureChunk(Integer id, String md5) {
        return getSignature(id, md5, null);
    }


    /**
     * 检查文件是否存在
     *
     * @param fileMd5  文件MD5
     * @param fileName 文件名
     * @return 如果存在返回 ok 不存在 签证 其他用户正在上传 busy
     */
    @Override
    public String checkFile(String fileMd5, String fileName) {
        String fileType = getFileType(fileName);
        if (fileType.equals("unknow")) {
            return "failure";
        }
        String key = redisKeys.checkFileExist(fileMd5);

        // 1. 第一级：Redis 快速过滤
        Pair status = redisBase.get(key, Pair.class);
        if ("ok".equals(status.getKey())) {
            userMediaService.saveToDb((Integer) status.getValue());
            return "ok"; // 秒传成功
        }
        if ("ing".equals(status.getKey())) {
            return "busy"; // 提示用户正在处理中，请稍后
        }

        // 2. 第二级：数据库元数据拦截（防止缓存失效后的穿透）
        Media media = getOne(new LambdaQueryWrapper<Media>()
                .eq(Media::getMd5, fileMd5).last("limit 1"));
        if (media != null) {
            redisBase.set(key, "ok"); // 补填缓存
            return "ok";
        }

        // 3. 第三级：加锁进行物理检查与发证
        RLock lock = redissonClient.getLock("lock:file:" + fileMd5);
        if (lock.tryLock()) {
            // Double Check: 拿锁后再次确认状态
            if (redisBase.get(key, String.class).equals("ok"))
                return "ok";

            // 物理检查：看 MinIO 里是否真的有残留文件
            StatObjectResponse stat = getStatFile(fileMd5, fileName);
            if (stat != null) {
                // 校验文件完整性（注意：分片上传的 ETag 包含横杠）
                if (stat.etag().contains(fileMd5)) {
                    // 异步转存并记录数据库（使用代理对象保证事务生效）
                    Integer id = applicationContext.getBean(MediaServiceImpl.class)
                            .saveToDb(fileMd5, fileName, (int) stat.size());
                    redisBase.set(key, Pair.of("ok", id));
                    return "ok";
                }
            }

            // 4. 确定没传过，开始发证
            String signature = getSignatureFile(fileMd5, fileName);

            // 设置为 "ING" 状态，有效期建议与签证有效期一致
            redisBase.set(key, "ing");

            // 投递延迟清理消息：如果 1 小时后还是 "ING"，说明上传失败，删掉 Redis 状态
            mqBase.sendDelayHourMessageToMusic(MqConfig.MEDIA_TEMP_KEY,
                    new DelTempMedia(null,key, minioConfig.music, getFilePath(fileMd5, fileName)));

            return signature;
        } else {
            return "busy";
        }

    }


    /**
     * 保存数据到 db
     *
     * @param md5      文件MD5
     * @param fileName 文件名
     * @param size     文件大小
     * @return 媒资 id
     */
    @Transactional
    public Integer saveToDb(String md5, String fileName, int size) {
        Integer userId = UserContext.getUser();
        String filePath = getFilePath(md5, fileName);
        Media media = new Media();
        media.setBucket(minioConfig.music);
        media.setPath(filePath);
        media.setUrl(minioConfig.music + filePath);
        media.setName(fileName);
        media.setType(getFileType(fileName));
        media.setSize(size);
        media.setMd5(md5);
//            http://192.168.124.8:9000/music/a3.png
        media.setStatus(AuditState.auditing);
        media.setUserId(userId);
        save(media);
        return media.getId();

        //todo 加入审核
    }

    private String getFileType(String fileName) {
        fileName = fileName.substring(fileName.lastIndexOf("."));
        if (audio.contains(fileName)) {
            return "audio";
        } else if (video.contains(fileName)) {
            return "video";
        } else if (pic.contains(fileName)) {
            return "pic";
        } else {
            return "unknow";
        }
    }


    /**
     * 检查分块文件是否存在
     *
     * @param id       分块索引
     * @param chunkMd5 分块的MD5
     * @param fileMd5  完整文件的MD5
     * @return 如果存在返回 ok 不存在 签证 其他用户正在上传 busy
     */
    @Override
    public String checkChunk(Integer id, String chunkMd5, String fileMd5) {
        String key = redisKeys.checkChunkExist(chunkMd5, id);

        // 1. 第一级：Redis 快速过滤
        String status = redisBase.get(key, String.class);
        if ("ok".equals(status)) {
            return "ok"; // 秒传成功
        }
        if ("ing".equals(status)) {
            return "busy"; // 提示用户正在处理中，请稍后
        }

        // 3. 第三级：加锁进行物理检查与发证
        RLock lock = redissonClient.getLock("lock:file:" + chunkMd5);
        if (lock.tryLock()) {
            // Double Check: 拿锁后再次确认状态
            if (redisBase.get(key, String.class).equals("ok"))
                return "ok";

            // 物理检查：看 MinIO 里是否真的有残留文件
            StatObjectResponse stat = getStatChunk(id, fileMd5);
            if (stat != null) {
                // 校验文件完整性（注意：分片上传的 ETag 包含横杠）
                if (stat.etag().contains(chunkMd5)) {
                    redisBase.set(key, "ok");
                    return "ok";
                }
            }

            // 4. 确定没传过，开始发证
            String signature = getSignatureChunk(id, fileMd5);

            // 设置为 "ING" 状态，有效期建议与签证有效期一致
            redisBase.set(key, "ing");

            // 投递延迟清理消息：如果 1 小时后还是 "ING"，说明上传失败，删掉 Redis 状态
            mqBase.sendDelayHourMessageToMusic(MqConfig.MEDIA_TEMP_KEY,
                    new DelTempMedia(null,key, minioConfig.music, getChunkPath(id, fileMd5)));

            return signature;
        } else {
            return "busy";
        }
    }


    /**
     * 检查分块是否都已上传
     *
     * @param total   分块的总数
     * @param fileMd5 完整的文件MD5
     * @return 是否都已上传
     */
    private boolean checkChunkNum(int total, String fileMd5) {
        ArrayList<String> keys = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            keys.add(redisKeys.checkChunkExist(fileMd5, i));
        }
        List<String> batch = redisBase.getBatch(keys, String.class);
        for (int i = 0; i < batch.size(); i++) {
            if (batch.get(i) == null) {
                StatObjectResponse statChunk = getStatChunk(i, fileMd5);
                if (statChunk == null || statChunk.etag().contains(fileMd5)) {
                    return false;
                }
                redisBase.set(keys.get(i), "ok");
            } else if (!"ok".equals(batch.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public R<Object> merge(int total, String fileMd5, String fileName) {
        if (!checkChunkNum(total, fileMd5)) {
            log.error("分块数量缺失");
            return R.failure("分块数量缺失");
        }
        ArrayList<ComposeSource> list = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            ComposeSource build = ComposeSource.builder()
                    .bucket(minioConfig.music)
                    .object(getChunkPath(i, fileMd5))
                    .build();
            list.add(build);
        }
        try {
            minioClient.composeObject(ComposeObjectArgs.builder()
                    .sources(list)
                    .bucket(minioConfig.music)
                    .object(getFilePath(fileMd5, fileName))
                    .build());
        } catch (Exception e) {
            log.error("md5:{},fileName:{}分块合并失败{}", fileMd5, fileName, e.getMessage());
            return R.failure("合并分块失败");
        }
        StatObjectResponse stat = getStatFile(fileMd5, fileName);
        if (stat == null || !stat.etag().contains(fileMd5)) {
            return R.failure("合并分块失败");
        }

        applicationContext.getBean(MediaServiceImpl.class)
                .saveToDb(fileMd5, fileName, (int) stat.size());

        return R.success();
    }

//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void del(String url) {
//        LambdaQueryWrapper<Media> qw = new LambdaQueryWrapper<Media>()
//                .eq(Media::getUrl, url);
//        Media one = getOne(qw);
//        if (one == null) {
//            return;
//        }
//        one.setRefNum(one.getRefNum() - 1);
//        if (one.getRefNum() <= 0) {
//            remove(qw);
//        } else {
//            updateById(one);
//        }
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void add(Media media) {
//        LambdaQueryWrapper<Media> qw = new LambdaQueryWrapper<Media>()
//                .eq(Media::getUrl, media.getUrl());
//        Media one = getOne(qw);
//        if (one == null) {
//            save(media);
//        } else {
//            one.setRefNum(one.getRefNum() + 1);
//            updateById(one);
//        }
//    }


}
