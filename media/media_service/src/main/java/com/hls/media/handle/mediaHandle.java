package com.hls.media.handle;

import com.hls.media.config.MinioConfig;
import com.hls.media.po.MediaTemp;
import com.hls.media.service.IMediaTempService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
@RequiredArgsConstructor
@Component
public class mediaHandle {

    private final MinioConfig minioConfig;
    private final MinioClient minioClient;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final IMediaTempService mediaTempService;

    @XxlJob("mediaTempClear")
    public void mediaTempClear(){
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        List<MediaTemp> task = mediaTempService.getTask();
        List<MediaTemp> list = task.stream().filter(v -> v.getId() % shardTotal == shardIndex).toList();
        threadPoolExecutor.execute(()->{
            clearTempMedia(list);
        });
    }



    private void clearTempMedia(List<MediaTemp> list) {
        if(list.isEmpty()){
            return;
        }
        List<DeleteObject> list1 = list.stream().filter(v -> {
            String path = v.getPath();
            StatObjectArgs build = StatObjectArgs.builder()
                    .bucket(minioConfig.temp)
                    .object(path)
                    .build();
            try {
                minioClient.statObject(build);
            } catch (Exception e) {
                log.info("定时清理对象不存在，path：{}", path);
                mediaTempService.addTryNum(Long.valueOf(v.getId()));
                return false;
            }
            return true;
        }).map(v -> new DeleteObject(v.getPath())).toList();
        RemoveObjectsArgs build = RemoveObjectsArgs.builder()
                .bucket(minioConfig.temp)
                .objects(list1)
                .build();

        Iterable<Result<DeleteError>> results = minioClient.removeObjects(build);
        //必须遍历了迭代器才能删除成功
        for (Result<DeleteError> result : results) {
            DeleteError error = null;
            try {
                error = result.get();
            } catch (Exception e) {
                log.error("minio批量删除文件失败：" + error.objectName() + "; " + error.message());
            }
        }

    }






}
