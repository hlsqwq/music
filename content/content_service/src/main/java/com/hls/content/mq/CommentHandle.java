package com.hls.content.mq;

import com.hls.base.config.MqConfig;
import com.hls.base.utils.RedisBase;
import com.hls.base.utils.RedisKeys;
import com.hls.content.dto.CommentMessage;
import com.hls.content.po.Comment;
import com.hls.content.utils.RedisHotUtil;
import com.hls.content.vo.CommentVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentHandle {


    private final RedisBase redisBase;
    private final RedisKeys redisKeys;
    private final RedisHotUtil redisHotUtil;


    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = MqConfig.COMMENT_QUEUE),
            exchange = @Exchange(name = MqConfig.EXCHANGE, type = ExchangeTypes.DIRECT), key = {
            MqConfig.COMMENT_KEY}))
    public void updateCommentHandle(CommentMessage commentMessage) {
        String hotKey = redisKeys.commentList(commentMessage.getType(), commentMessage.getId());
        redisBase.lock(hotKey, new Runnable() {
            @Override
            public void run() {
                List<CommentVo> list = commentMessage.getList();
                List<String> keys = list.stream()
                        .map(v -> redisKeys.commentLike(commentMessage.getType(), v.getId()))
                        .toList();
                List<Long> batch = redisBase.getBatch(keys, Long.class);
                HashMap<String, Object> map = new HashMap<>();
                for (int i = 0; i < batch.size(); i++) {
                    if (batch.get(i) == null) {
                        batch.set(i, list.get(i).getLikeNum());
                        map.put(keys.get(i), list.get(i).getLikeNum());
                    }
                }
                redisBase.setBatch(map);
                List<Double> hots = batch.stream().map(redisHotUtil::culCommentHot).toList();
                HashSet<ZSetOperations.TypedTuple<Object>> set = new HashSet<>();
                for (int i = 0; i < hots.size(); i++) {
                    set.add(new DefaultTypedTuple<>(list.get(i).getId(), hots.get(i)));
                }
                redisBase.zSetAddBatch(hotKey, set);
            }
        });
    }
}
