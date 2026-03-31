package com.hls.canal.service;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CannalClient implements InitializingBean {

    private final static int BATCH_SIZE = 1000;

    private final CanalConnector connector;

    @Override
    public void afterPropertiesSet() {
        // 把监听逻辑放到独立线程，避免阻塞Spring容器启动
        new Thread(this::startCanalListener, "canal-listener-thread").start();
    }

    /**
     * 独立的监听方法，包含重连逻辑，运行在单独线程中
     */
    private void startCanalListener() {
        // 外层循环：异常后自动重连
        while (true) {
            try {
                // 打开连接
                connector.connect();
                // 订阅所有库所有表
                connector.subscribe(".*\\..*");
                // 回滚未ack的消息
                connector.rollback();
                log.info("Canal客户端连接成功，开始监听binlog...");

                // 内层循环：正常监听binlog
                while (true) {
                    Message message = connector.getWithoutAck(BATCH_SIZE);
                    long batchId = message.getId();
                    int size = message.getEntries().size();

                    if (batchId == -1 || size == 0) {
                        Thread.sleep(2000); // 无数据时休眠2秒
                        continue;
                    }

                    // 处理数据变更
                    processDataChange(message.getEntries());
                    // 确认消息消费
                    connector.ack(batchId);
                }
            } catch (InterruptedException e) {
                // 线程休眠被中断，恢复中断标记并退出（优雅关闭）
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // 捕获所有异常，打印日志后重连
                System.err.println("Canal监听异常，3秒后尝试重连：" + e.getMessage());
                e.printStackTrace();
                // 异常时断开连接
                if (connector != null) {
                    try {
                        connector.disconnect();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                // 休眠3秒后重连
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * 处理数据变更，同步到Redis和ES
     *
     * @param entrys
     * @throws InvalidProtocolBufferException
     */
    public void processDataChange(List<Entry> entrys) throws InvalidProtocolBufferException {
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONEND
                    || entry.getEntryType() == EntryType.TRANSACTIONBEGIN) {
                continue;
            }

            String tableName = entry.getHeader().getTableName();

            // 处理song、singer、album、mv表，同步到ES
            //todo es
        }
    }


}