package com.hls.media.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 转码调度核心类：自动判断文件类型，分发转码任务到线程池
 */
@RequiredArgsConstructor
@Component
public class FileTranscodeManager {
    private static final Logger log = LoggerFactory.getLogger(FileTranscodeManager.class);


    private final MinioClient minioClient;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ImageTranscoder imageTranscoder;
    private final MediaTranscoder mediaTranscoder;

    // 文件类型枚举
    public enum FileType {
        IMAGE, AUDIO, VIDEO, UNKNOWN
    }

    // 魔数映射表（核心：文件头字节 → 文件类型）
    private static final Map<String, FileType> MAGIC_NUMBER_MAP = new HashMap<>();

    static {
        // 图片魔数
        MAGIC_NUMBER_MAP.put("89504E47", FileType.IMAGE); // PNG
        MAGIC_NUMBER_MAP.put("FFD8FF", FileType.IMAGE);   // JPG/JPEG
        MAGIC_NUMBER_MAP.put("47494638", FileType.IMAGE); // GIF
        MAGIC_NUMBER_MAP.put("424D", FileType.IMAGE);     // BMP
        // 音频魔数
        MAGIC_NUMBER_MAP.put("494433", FileType.AUDIO);   // MP3
        MAGIC_NUMBER_MAP.put("52494646", FileType.AUDIO); // WAV
        MAGIC_NUMBER_MAP.put("664C6143", FileType.AUDIO); // FLAC
        // 视频魔数
        MAGIC_NUMBER_MAP.put("0000001866747970", FileType.VIDEO); // MP4
        MAGIC_NUMBER_MAP.put("52494646", FileType.VIDEO);         // AVI
        MAGIC_NUMBER_MAP.put("464C5601", FileType.VIDEO);         // FLV
        MAGIC_NUMBER_MAP.put("1A45DFA3", FileType.VIDEO);         // MKV
    }

    /**
     * 核心入口：自动判断文件类型并异步转码
     *
     * @param sourceBucket    源文件桶名
     * @param sourceObjectKey 源文件MinIO路径（如 "temp/test.png"）
     * @param targetBucket    转码后文件桶名
     * @param targetObjectKey 转码后文件路径（如 "music/test.jpg/mp3/mp4"）
     * @return Future<Boolean> 转码结果（true=成功，false=失败）
     */
    public Future<Boolean> autoTranscode(String sourceBucket, String sourceObjectKey,
                                         String targetBucket, String targetObjectKey) {
        // 提交到线程池异步执行
        return threadPoolExecutor.submit(() -> {
            try {
                // 步骤1：判断文件类型
                FileType fileType = getFileType(sourceBucket, sourceObjectKey);
                if (FileType.UNKNOWN.equals(fileType)) {
                    log.error("文件类型无法识别，终止转码：{}", sourceObjectKey);
                    return false;
                }

                // 步骤2：分发转码任务
                boolean result = switch (fileType) {
                    case IMAGE -> imageTranscoder.transcodeToJpg(sourceBucket, sourceObjectKey,
                            targetBucket, targetObjectKey);
                    case AUDIO -> mediaTranscoder.transcodeToMp3(sourceBucket, sourceObjectKey,
                            targetBucket, targetObjectKey);
                    case VIDEO -> mediaTranscoder.transcodeToMp4(sourceBucket, sourceObjectKey,
                            targetBucket, targetObjectKey);
                    default -> false;
                };

                log.info("转码完成：{} → {}，结果：{}", sourceObjectKey, targetObjectKey, result);
                return result;
            } catch (Exception e) {
                log.error("转码失败：{}", sourceObjectKey, e);
                return false;
            }
        });
    }

    /**
     * 魔数方式判断MinIO文件类型
     */
    private FileType getFileType(String bucket, String objectKey) {
        try (InputStream is = minioClient.getObject(
                io.minio.GetObjectArgs.builder().bucket(bucket).object(objectKey).build()
        )) {
            // 读取文件前8个字节（覆盖所有常见魔数）
            byte[] header = new byte[8];
            int readLen = is.read(header);
            if (readLen <= 0) {
                return FileType.UNKNOWN;
            }

            // 字节转十六进制
            String hexHeader = bytesToHex(header, readLen).toUpperCase();
            // 匹配魔数
            for (Map.Entry<String, FileType> entry : MAGIC_NUMBER_MAP.entrySet()) {
                String magic = entry.getKey();
                if (hexHeader.startsWith(magic)) {
                    // 特殊处理：WAV(音频)/AVI(视频)魔数相同，补充判断
                    if (magic.equals("52494646")) {
                        return isAudioWav(is) ? FileType.AUDIO : FileType.VIDEO;
                    }
                    return entry.getValue();
                }
            }
            return FileType.UNKNOWN;
        } catch (Exception e) {
            log.error("判断文件类型失败：bucket={}, objectKey={}", bucket, objectKey, e);
            return FileType.UNKNOWN;
        }
    }

    // 辅助：区分WAV(音频)和AVI(视频)
    private boolean isAudioWav(InputStream is) throws Exception {
        is.reset(); // 重置流指针
        byte[] buffer = new byte[20];
        is.read(buffer);
        String headerStr = new String(buffer);
        return headerStr.contains("WAVE"); // WAV文件包含"WAVE"标识
    }

    // 辅助：字节数组转十六进制字符串
    private String bytesToHex(byte[] bytes, int length) {
        StringBuilder hexStr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hexStr.append("0");
            }
            hexStr.append(hex);
        }
        return hexStr.toString();
    }
}