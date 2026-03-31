package com.hls.search.doc;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "hotel") // 对应你刚才 PUT 的索引名
public class HotelDoc {
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", copyTo = "all")
    private String name;
    
    @Field(type = FieldType.Keyword)
    private String brand;
    
    @Field(type = FieldType.Integer)
    private Integer price;
    
    // 地理位置需要特殊处理
    private String location; 
}