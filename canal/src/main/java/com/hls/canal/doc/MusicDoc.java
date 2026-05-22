package com.hls.canal.doc;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(indexName = "music")
public class MusicDoc implements Serializable {

    @Id
    private String id;

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

    // Song
    @Field(type = FieldType.Integer, index = false)
    private Integer duration;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String singerName;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String albumName;

    // Singer
    @Field(type = FieldType.Integer, index = false)
    private Integer songNum;
    @Field(type = FieldType.Integer, index = false)
    private Integer albumNum;
    @Field(type = FieldType.Integer, index = false)
    private Integer mvNum;

    // SongList
    @Field(type = FieldType.Integer, index = false)
    private Integer userId;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String userName;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
