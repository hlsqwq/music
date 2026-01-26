package com.hls.media.config;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * 音视频转码类：MinIO音频→MP3、视频→MP4（FFmpeg+管道流，无本地文件）
 */
@Component
public class MediaTranscoder {
    private static final Logger log = LoggerFactory.getLogger(MediaTranscoder.class);

    // FFmpeg路径（未配置环境变量则改绝对路径，如 "D:/ffmpeg/bin/ffmpeg.exe"）
    private static final String FFMPEG_PATH = "ffmpeg";

    @Autowired
    private MinioClient minioClient;

    /**
     * 音频转MP3（AAC/FLAC/WAV等→MP3）
     */
    public boolean transcodeToMp3(String sourceBucket, String sourceObjectKey,
                                  String targetBucket, String targetObjectKey) throws Exception {
        // FFmpeg转MP3命令
        String[] cmd = {
                FFMPEG_PATH,
                "-i", "pipe:0",          // 管道读入源音频
                "-c:a", "libmp3lame",    // MP3编码
                "-b:a", "128k",          // 音频码率
                "-f", "mp3",             // 输出格式
                "-y",                    // 覆盖（管道流无文件，仅兼容）
                "pipe:1"                 // 管道输出MP3
        };
        return executeFfmpegTranscode(cmd, sourceBucket, sourceObjectKey, targetBucket, targetObjectKey, "audio/mpeg");
    }

    /**
     * 视频转MP4（FLV/AVI/MKV等→MP4，H.264+AAC）
     */
    public boolean transcodeToMp4(String sourceBucket, String sourceObjectKey,
                                  String targetBucket, String targetObjectKey) throws Exception {
        // FFmpeg转MP4命令（兼容性最优）
        String[] cmd = {
                FFMPEG_PATH,
                "-i", "pipe:0",          // 管道读入源视频
                "-c:v", "libx264",       // H.264视频编码
                "-preset", "medium",     // 速度/质量平衡
                "-crf", "23",            // 视频质量（18-28最佳）
                "-c:a", "aac",           // AAC音频编码
                "-b:a", "128k",          // 音频码率
                "-f", "mp4",             // 输出格式
                "-y",                    // 覆盖
                "pipe:1"                 // 管道输出MP4
        };
        return executeFfmpegTranscode(cmd, sourceBucket, sourceObjectKey, targetBucket, targetObjectKey, "video/mp4");
    }

    /**
     * 通用FFmpeg转码执行逻辑
     */
    private boolean executeFfmpegTranscode(String[] ffmpegCmd,
                                           String sourceBucket, String sourceObjectKey,
                                           String targetBucket, String targetObjectKey,
                                           String contentType) throws Exception {
        // 创建管道流
        PipedOutputStream ffmpegInPos = new PipedOutputStream();
        PipedInputStream ffmpegInPis = new PipedInputStream(ffmpegInPos);
        PipedOutputStream ffmpegOutPos = new PipedOutputStream();
        PipedInputStream ffmpegOutPis = new PipedInputStream(ffmpegOutPos);

        // 线程1：MinIO读源文件 → 写入FFmpeg输入管道
        Thread minioReadThread = new Thread(() -> {
            try (InputStream is = minioClient.getObject(
                    GetObjectArgs.builder().bucket(sourceBucket).object(sourceObjectKey).build()
            ); OutputStream os = ffmpegInPos) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
            } catch (Exception e) {
                log.error("MinIO读流失败", e);
                throw new RuntimeException(e);
            } finally {
                try {
                    ffmpegInPos.close();
                } catch (IOException e) {
                    log.error("关闭FFmpeg输入管道失败", e);
                }
            }
        });

        // 线程2：启动FFmpeg转码 → 输出到管道
        Thread ffmpegThread = new Thread(() -> {
            try {
                Process process = new ProcessBuilder(ffmpegCmd)
                        .redirectError(ProcessBuilder.Redirect.PIPE) // 重定向错误日志
                        .start();

                // 打印FFmpeg日志（排查问题用）
                new Thread(() -> {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            log.debug("FFmpeg日志：{}", line);
                        }
                    } catch (IOException e) {
                        log.error("读取FFmpeg日志失败", e);
                    }
                }).start();

                // FFmpeg输入：管道→进程
                try (InputStream is = ffmpegInPis; OutputStream os = process.getOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    os.flush();
                    os.close();
                }

                // FFmpeg输出：进程→管道
                try (InputStream is = process.getInputStream(); OutputStream os = ffmpegOutPos) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                }

                // 检查FFmpeg退出码（0=成功）
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("FFmpeg转码失败，退出码：" + exitCode);
                }
            } catch (Exception e) {
                log.error("FFmpeg转码失败", e);
                throw new RuntimeException(e);
            } finally {
                try {
                    ffmpegOutPos.close();
                } catch (IOException e) {
                    log.error("关闭FFmpeg输出管道失败", e);
                }
            }
        });

        // 线程3：管道读转码后数据 → 写入MinIO
        Thread minioWriteThread = new Thread(() -> {
            try (InputStream is = ffmpegOutPis) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(targetBucket)
                                .object(targetObjectKey)
                                .stream(is, is.available(), -1)
                                .contentType(contentType)
                                .build()
                );
            } catch (Exception e) {
                log.error("MinIO写入转码文件失败", e);
                throw new RuntimeException(e);
            } finally {
                try {
                    ffmpegOutPis.close();
                } catch (IOException e) {
                    log.error("关闭MinIO输入管道失败", e);
                }
            }
        });

        // 启动线程（顺序：写→转码→读）
        minioWriteThread.start();
        ffmpegThread.start();
        minioReadThread.start();

        // 等待线程完成
        minioReadThread.join();
        ffmpegThread.join();
        minioWriteThread.join();

        return true;
    }
}