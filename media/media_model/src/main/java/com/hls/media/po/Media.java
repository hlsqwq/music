package com.hls.media.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 媒体文件信息表
 * </p>
 *
 * @author hls
 * @since 2026-03-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("media")
public class Media implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 第一个上传的用户id
     */
    private Integer userId;

    /**
     * 存储桶名称（如OSS/COS的bucket）
     */
    private String bucket;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件URL
     */
    private String url;

    /**
     * 文件名
     */
    private String name;

    private String type;

    /**
     * 文件的MD5
     */
    private String md5;

    /**
     * 媒体审核状态
     */
    private String status;

    /**
     * 文件大小
     */
    private Integer size;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
