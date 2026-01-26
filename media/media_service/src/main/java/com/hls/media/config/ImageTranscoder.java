package com.hls.media.config;

import net.coobird.thumbnailator.Thumbnails;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图片转码类：MinIO图片→JPG（管道流+Thumbnails，无本地文件）
 */
@Component
public class ImageTranscoder {
    private static final Logger log = LoggerFactory.getLogger(ImageTranscoder.class);

    @Autowired
    private MinioClient minioClient;

    /**
     * 图片转JPG
     */
    public boolean transcodeToJpg(String sourceBucket, String sourceObjectKey,
                                  String targetBucket, String targetObjectKey) throws Exception {
        // 创建管道流（读写分离，避免阻塞）
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);

        // 线程1：读取MinIO图片 → 转码 → 写入管道
        Thread transcodeThread = new Thread(() -> {
            try (InputStream sourceIs = minioClient.getObject(
                    GetObjectArgs.builder().bucket(sourceBucket).object(sourceObjectKey).build()
            ); OutputStream os = pos) {

                // 读取源图片
                BufferedImage sourceImage = ImageIO.read(sourceIs);
                if (sourceImage == null) {
                    throw new IOException("无效图片文件：" + sourceObjectKey);
                }

                // 处理透明背景（PNG转JPG填充白色）
                BufferedImage jpgImage = new BufferedImage(
                        sourceImage.getWidth(), sourceImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB
                );
                Graphics2D g2d = jpgImage.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, sourceImage.getWidth(), sourceImage.getHeight());
                g2d.drawImage(sourceImage, 0, 0, null);
                g2d.dispose();

                // 转码为JPG写入管道
                Thumbnails.of(jpgImage)
                        .outputFormat("jpg")
                        .outputQuality(0.95) // JPG质量（0.0-1.0）
                        .toOutputStream(os);

            } catch (Exception e) {
                log.error("图片转码失败：{}", sourceObjectKey, e);
                throw new RuntimeException(e);
            } finally {
                try {
                    pos.close();
                } catch (IOException e) {
                    log.error("关闭图片转码管道失败", e);
                }
            }
        });

        // 线程2：从管道读取JPG → 写入MinIO
        Thread uploadThread = new Thread(() -> {
            try (InputStream is = pis) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(targetBucket)
                                .object(targetObjectKey)
                                .stream(is, is.available(), -1)
                                .contentType("image/jpeg")
                                .build()
                );
            } catch (Exception e) {
                log.error("图片写入MinIO失败：{}", targetObjectKey, e);
                throw new RuntimeException(e);
            } finally {
                try {
                    pis.close();
                } catch (IOException e) {
                    log.error("关闭MinIO写入管道失败", e);
                }
            }
        });

        // 启动线程并等待完成
        uploadThread.start();
        transcodeThread.start();
        transcodeThread.join();
        uploadThread.join();

        return true;
    }
}