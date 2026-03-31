package com.hls.content.job;

import com.hls.base.config.MqConfig;
import com.hls.base.utils.MqBase;
import com.hls.base.utils.RedisKeys;
import com.hls.content.dto.HotDataDto;
import com.hls.content.service.ISongService;
import com.hls.content.utils.RedisHotUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotDataJob {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisHotUtil redisHotUtil;
    private final RabbitTemplate rabbitTemplate;
    private final MqBase mqBase;
    private final ISongService songService;

    // 批量处理大小
    private static final int BATCH_SIZE = 5000;

    @XxlJob("hotDataJob")
    public void hotDataJob() throws Exception {
        log.info("开始执行热度数据处理任务");

        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();


        //更新歌曲
        List<HotDataDto> songFavorite = redisHotUtil.getTask(shardIndex, shardTotal,
                RedisKeys.TableType.song, RedisKeys.DoType.favorite);
        List<HotDataDto> songPlay = redisHotUtil.getTask(shardIndex, shardTotal,
                RedisKeys.TableType.song, RedisKeys.DoType.play);
        List<HotDataDto> songComment = redisHotUtil.getTask(shardIndex, shardTotal,
                RedisKeys.TableType.song, RedisKeys.DoType.comment);
        mqBase.sendMessageToMusic(MqConfig.HOT_SONG_KEY, List.of(songFavorite, songPlay, songComment));

        //更新mv
        List<HotDataDto> task1 = redisHotUtil.getTask(shardIndex, shardTotal,
                RedisKeys.TableType.mv, RedisKeys.DoType.play);
        List<HotDataDto> task2 = redisHotUtil.getTask(shardIndex, shardTotal,
                RedisKeys.TableType.mv, RedisKeys.DoType.like);
        List<HotDataDto> task = redisHotUtil.getTask(shardIndex, shardTotal,
                RedisKeys.TableType.mv, RedisKeys.DoType.comment);
        List<HotDataDto> task3 = redisHotUtil.getTask(shardIndex, shardTotal,
                RedisKeys.TableType.mv, RedisKeys.DoType.favorite);
        mqBase.sendMessageToMusic(MqConfig.HOT_MV_KEY, List.of(task1, task2, task, task3));

        //更新singer
        List<HotDataDto> singerFans = redisHotUtil.getTask(shardIndex, shardTotal,
                RedisKeys.TableType.singer, RedisKeys.DoType.fans);
        //todo

        //更新album
        List<HotDataDto> albumFavorite = redisHotUtil.getTask(shardIndex, shardTotal,
                RedisKeys.TableType.album, RedisKeys.DoType.favorite);
        //todo

        log.info("热度数据发送执行完成");
        XxlJobHelper.handleSuccess("热度数据发送执行完成");
    }

}