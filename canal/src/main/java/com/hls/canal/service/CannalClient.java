package com.hls.canal.service;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hls.base.utils.RedisBase;
import com.hls.base.utils.RedisKeys;
import com.hls.canal.doc.MusicDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class CannalClient implements InitializingBean {

    private final static int BATCH_SIZE = 1000;
    private static final int LEADERBOARD_SIZE = 500;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CanalConnector connector;
    private final ElasticsearchOperations elasticsearchOperations;
    private final RedisBase redisBase;
    private final RedisKeys redisKeys;

    @Override
    public void afterPropertiesSet() {
        new Thread(this::startCanalListener, "canal-listener-thread").start();
    }

    private void startCanalListener() {
        while (true) {
            try {
                connector.connect();
                connector.subscribe(".*\\..*");
                connector.rollback();
                log.info("Canal客户端连接成功，开始监听binlog...");

                while (true) {
                    Message message = connector.getWithoutAck(BATCH_SIZE);
                    long batchId = message.getId();
                    int size = message.getEntries().size();

                    if (batchId == -1 || size == 0) {
                        Thread.sleep(2000);
                        continue;
                    }

                    processDataChange(message.getEntries());
                    connector.ack(batchId);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Canal监听异常，3秒后尝试重连：{}", e.getMessage());
                if (connector != null) {
                    try {
                        connector.disconnect();
                    } catch (Exception ex) {
                        log.error("Canal断开连接异常", ex);
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public void processDataChange(List<Entry> entrys) throws InvalidProtocolBufferException {
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONEND
                    || entry.getEntryType() == EntryType.TRANSACTIONBEGIN) {
                continue;
            }

            Header header = entry.getHeader();
            String tableName = header.getTableName();
            EventType eventType = header.getEventType();

            // 只处理目标表
            if (!"song".equals(tableName) && !"singer".equals(tableName)
                    && !"album".equals(tableName) && !"mv".equals(tableName)
                    && !"song_list".equals(tableName) && !"comment".equals(tableName)) {
                continue;
            }

            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
            for (RowData rowData : rowChange.getRowDatasList()) {
                Map<String, String> columns;
                if (eventType == EventType.DELETE) {
                    columns = columnsToMap(rowData.getBeforeColumnsList());
                } else {
                    columns = columnsToMap(rowData.getAfterColumnsList());
                }

                try {
                    switch (tableName) {
                        case "song" -> handleSongChange(columns, eventType);
                        case "singer" -> handleSingerChange(columns, eventType);
                        case "album" -> handleAlbumChange(columns, eventType);
                        case "mv" -> handleMvChange(columns, eventType);
                        case "song_list" -> handleSongListChange(columns, eventType);
                        case "comment" -> handleCommentChange(columns, eventType);
                    }
                } catch (Exception e) {
                    log.error("处理表 {} 变更失败: {}", tableName, e.getMessage(), e);
                }
            }
        }
    }

    // ==================== 表变更处理器 ====================

    private void handleSongChange(Map<String, String> columns, EventType eventType) {
        int id = getInt(columns, "id");
        String docType = "song";
        String docId = docType + "_" + id;

        if (eventType == EventType.DELETE) {
            deleteFromEs(docId);
            redisBase.zSetDelete(redisKeys.getSongTop(), id);
            return;
        }

        MusicDoc doc = new MusicDoc()
                .setId(docId)
                .setDocType(docType)
                .setDocId(id)
                .setName(columns.get("name"))
                .setAvatarUrl(columns.get("avatar_url"))
                .setPlayNum(getLong(columns, "play_num"))
                .setFavoriteNum(getLong(columns, "favorite_num"))
                .setCommentNum(getLong(columns, "comment_num"))
                .setHot(getLong(columns, "hot"))
                .setDuration(getInt(columns, "duration"))
                .setSingerName(columns.get("singer_name"))
                .setAlbumName(columns.get("album_name"))
                .setCreateTime(parseDateTime(columns.get("create_time")));

        saveToEs(doc);

        // 同步到Redis歌曲排行榜
        Long hot = doc.getHot();
        if (hot != null) {
            addToLeaderboard(redisKeys.getSongTop(), id, hot);
        }
    }

    private void handleSingerChange(Map<String, String> columns, EventType eventType) {
        int id = getInt(columns, "id");
        String docType = "singer";
        String docId = docType + "_" + id;

        if (eventType == EventType.DELETE) {
            deleteFromEs(docId);
            redisBase.zSetDelete(redisKeys.getSingerTop(null), id);
            return;
        }

        MusicDoc doc = new MusicDoc()
                .setId(docId)
                .setDocType(docType)
                .setDocId(id)
                .setName(columns.get("name"))
                .setAvatarUrl(columns.get("avatar_url"))
                .setFansNum(getLong(columns, "fans_num"))
                .setHot(getLong(columns, "hot"))
                .setSongNum(getInt(columns, "song_num"))
                .setAlbumNum(getInt(columns, "album_num"))
                .setMvNum(getInt(columns, "mv_num"))
                .setCreateTime(parseDateTime(columns.get("create_time")));

        saveToEs(doc);

        Long hot = doc.getHot();
        if (hot != null) {
            addToLeaderboard(redisKeys.getSingerTop(null), id, hot);
        }
    }

    private void handleAlbumChange(Map<String, String> columns, EventType eventType) {
        int id = getInt(columns, "id");
        String docType = "album";
        String docId = docType + "_" + id;

        if (eventType == EventType.DELETE) {
            deleteFromEs(docId);
            redisBase.zSetDelete(redisKeys.getAlbumTop(), id);
            return;
        }

        MusicDoc doc = new MusicDoc()
                .setId(docId)
                .setDocType(docType)
                .setDocId(id)
                .setName(columns.get("name"))
                .setAvatarUrl(columns.get("avatar_url"))
                .setFavoriteNum(getLong(columns, "favorite_num"))
                .setHot(getLong(columns, "hot"))
                .setCreateTime(parseDateTime(columns.get("create_time")));

        saveToEs(doc);

        Long hot = doc.getHot();
        if (hot != null) {
            addToLeaderboard(redisKeys.getAlbumTop(), id, hot);
        }
    }

    private void handleMvChange(Map<String, String> columns, EventType eventType) {
        int id = getInt(columns, "id");
        String docType = "mv";
        String docId = docType + "_" + id;

        if (eventType == EventType.DELETE) {
            deleteFromEs(docId);
            redisBase.zSetDelete(redisKeys.getMvTop(), id);
            return;
        }

        MusicDoc doc = new MusicDoc()
                .setId(docId)
                .setDocType(docType)
                .setDocId(id)
                .setName(columns.get("name"))
                .setAvatarUrl(columns.get("avatar_url"))
                .setPlayNum(getLong(columns, "play_num"))
                .setLikeNum(getLong(columns, "like_num"))
                .setCommentNum(getLong(columns, "comment_num"))
                .setFavoriteNum(getLong(columns, "favorite_num"))
                .setHot(getLong(columns, "hot"))
                .setSingerName(columns.get("singer_name"))
                .setCreateTime(parseDateTime(columns.get("create_time")));

        saveToEs(doc);

        Long hot = doc.getHot();
        if (hot != null) {
            addToLeaderboard(redisKeys.getMvTop(), id, hot);
        }
    }

    private void handleSongListChange(Map<String, String> columns, EventType eventType) {
        int id = getInt(columns, "id");
        String docType = "songlist";
        String docId = docType + "_" + id;

        if (eventType == EventType.DELETE) {
            deleteFromEs(docId);
            redisBase.zSetDelete(redisKeys.getSongListTop(), id);
            return;
        }

        MusicDoc doc = new MusicDoc()
                .setId(docId)
                .setDocType(docType)
                .setDocId(id)
                .setName(columns.get("name"))
                .setAvatarUrl(columns.get("avatar_url"))
                .setPlayNum(getLong(columns, "play_num"))
                .setFavoriteNum(getLong(columns, "favorite_num"))
                .setCommentNum(getLong(columns, "comment_num"))
                .setHot(getLong(columns, "hot"))
                .setUserId(getInt(columns, "user_id"))
                .setCreateTime(parseDateTime(columns.get("create_time")));

        saveToEs(doc);

        Long hot = doc.getHot();
        if (hot != null) {
            addToLeaderboard(redisKeys.getSongListTop(), id, hot);
        }
    }

    /**
     * 评论变更：同步评论排行榜到Redis
     * 评论的objType标识是song还是mv评论，typeId是对应的songId或mvId
     */
    private void handleCommentChange(Map<String, String> columns, EventType eventType) {
        int id = getInt(columns, "id");
        String objType = columns.get("obj_type");
        int typeId = getInt(columns, "type_id");
        Long hot = getLong(columns, "hot");
        Long likeNum = getLong(columns, "like_num");

        // 构建评论排行榜key
        RedisKeys.TableType tableType = "song".equals(objType)
                ? RedisKeys.TableType.song
                : RedisKeys.TableType.mv;
        String commentListKey = redisKeys.commentList(tableType, typeId);
        String commentLikeKey = redisKeys.commentLike(tableType, typeId);

        if (eventType == EventType.DELETE) {
            redisBase.zSetDelete(commentListKey, id);
            redisBase.zSetDelete(commentLikeKey, id);
            return;
        }

        // 同步评论热度排行榜
        if (hot != null) {
            redisBase.zSetAdd(commentListKey, id, hot);
            redisBase.zSetDeleteTail(commentListKey, LEADERBOARD_SIZE);
        }
        // 同步评论点赞排行榜
        if (likeNum != null) {
            redisBase.zSetAdd(commentLikeKey, id, likeNum);
            redisBase.zSetDeleteTail(commentLikeKey, LEADERBOARD_SIZE);
        }
    }

    // ==================== ES和Redis操作 ====================

    private void saveToEs(MusicDoc doc) {
        try {
            elasticsearchOperations.save(doc, IndexCoordinates.of("music"));
            log.debug("ES同步成功: {}", doc.getId());
        } catch (Exception e) {
            log.error("ES同步失败: {}, 错误: {}", doc.getId(), e.getMessage());
        }
    }

    private void deleteFromEs(String docId) {
        try {
            elasticsearchOperations.delete(docId, IndexCoordinates.of("music"));
            log.debug("ES删除成功: {}", docId);
        } catch (Exception e) {
            log.error("ES删除失败: {}, 错误: {}", docId, e.getMessage());
        }
    }

    private void addToLeaderboard(String key, int id, double score) {
        redisBase.zSetAdd(key, id, score);
        redisBase.zSetDeleteTail(key, LEADERBOARD_SIZE);
    }

    // ==================== 工具方法 ====================

    private Map<String, String> columnsToMap(List<Column> columns) {
        Map<String, String> map = new HashMap<>();
        for (Column column : columns) {
            map.put(column.getName(), column.getValue());
        }
        return map;
    }

    private int getInt(Map<String, String> columns, String key) {
        String value = columns.get(key);
        if (value == null || value.isEmpty()) return 0;
        return Integer.parseInt(value);
    }

    private Long getLong(Map<String, String> columns, String key) {
        String value = columns.get(key);
        if (value == null || value.isEmpty()) return null;
        return Long.parseLong(value);
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return LocalDateTime.parse(value, FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
}
