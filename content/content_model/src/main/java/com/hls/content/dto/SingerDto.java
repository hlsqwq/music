package com.hls.content.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 歌手信息表
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingerDto {


    /**
     * 歌手姓名
     */
    private String name;

    /**
     * 歌手头像URL
     */
    private String avatar;


//    /**
//     * 发布歌曲数量
//     */
//    private Integer songNum;
//
//    /**
//     * 发布专辑数量
//     */
//    private Integer albumNum;
//
//    /**
//     * 发布MV数量
//     */
//    private Integer mvNum;

    /**
     * 歌手简介
     */
    private String introduction;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
