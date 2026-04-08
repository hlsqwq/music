package com.hls.search.doc;

import com.hls.base.utils.RedisKeys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(indexName = "music")
public class MusicDoc implements Serializable {

    @Id
    private String id; // 统一 ID 标识

    /**
     * 类型：song/singer/album/mv/songlist
     */
    @Field(type = FieldType.Keyword)
    private String docType;
    @Field(type = FieldType.Integer, index = false)
    private Integer docId;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;

    @Field(type = FieldType.Keyword, index = false)
    private String avatarUrl;

    @Field(type = FieldType.Long, index = false)
    private Long playNum;
    @Field(type = FieldType.Long, index = false)
    private Long likeNum;
    @Field(type = FieldType.Long, index = false)
    private Long favoriteNum;
    @Field(type = FieldType.Long, index = false)
    private Long commentNum;
    @Field(type = FieldType.Long, index = false)
    private Long fansNum;
    @Field(type = FieldType.Long)
    private Long hot;


    // --- Song ---
    @Field(type = FieldType.Integer, index = false)
    private Integer duration;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String singerName;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String albumName;


    // --- Singer ---
    @Field(type = FieldType.Integer, index = false)
    private Integer songNum;
    @Field(type = FieldType.Integer, index = false)
    private Integer albumNum;
    @Field(type = FieldType.Integer, index = false)
    private Integer mvNum;

    // --- Album ---
    // --- MV ---

    // --- SongList ---
    @Field(type = FieldType.Integer, index = false)
    private Integer userId;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String userName;


    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}




//PUT /music
//{
//    "settings": {
//    "index": {
//        "number_of_shards": 3,
//                "number_of_replicas": 1,
//                "analysis": {
//            "analyzer": {
//                "music_analyzer": {
//                    "type": "ik_max_word"
//                }
//            }
//        }
//    }
//},
//    "mappings": {
//    "properties": {
//        // 统一 ID 标识，支持字符串格式如 "song_101"
//        "id": { "type": "keyword" },
//
//        // 类型过滤：用于区分当前文档是歌曲、歌手还是歌单
//        "docType": { "type": "keyword" },
//
//        // 业务 ID 与 资源地址 (index: false 仅存储不搜索)
//        "docId": { "type": "integer", "index": false },
//        "avatarUrl": { "type": "keyword", "index": false },
//
//        // 统计数据 (index: false)
//        "playNum": { "type": "long", "index": false },
//        "likeNum": { "type": "long", "index": false },
//        "favoriteNum": { "type": "long", "index": false },
//        "commentNum": { "type": "long", "index": false },
//        "fansNum": { "type": "long", "index": false },
//
//        // 核心搜索字段
//        "name": {
//            "type": "text",
//                    "analyzer": "ik_max_word",
//                    "fields": {
//                "keyword": { "type": "keyword", "ignore_above": 256 }
//            }
//        },
//
//        // 热度排序字段
//        "hotNum": { "type": "long" },
//
//        // --- Song 扩展信息 ---
//        "duration": { "type": "integer", "index": false },
//        "singerName": {
//            "type": "text",
//                    "analyzer": "ik_smart",
//                    "fields": { "keyword": { "type": "keyword" } }
//        },
//        "albumName": {
//            "type": "text",
//                    "analyzer": "ik_smart",
//                    "fields": { "keyword": { "type": "keyword" } }
//        },
//
//        // --- Singer 扩展信息 ---
//        "songNum": { "type": "integer", "index": false },
//        "albumNum": { "type": "integer", "index": false },
//        "mvNum": { "type": "integer", "index": false },
//
//        // --- SongList 扩展信息 ---
//        "userId": { "type": "integer", "index": false },
//        "userName": {
//            "type": "text",
//                    "analyzer": "ik_smart",
//                    "fields": { "keyword": { "type": "keyword" } }
//        },
//
//        // 时间字段，匹配 yyyy-MM-dd HH:mm:ss
//        "createTime": {
//            "type": "date",
//                    "format": "yyyy-MM-dd HH:mm:ss"
//        }
//    }
//}
//}