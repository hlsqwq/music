package com.hls.media.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 媒体文件信息表
 * </p>
 *
 * @author hls
 * @since 2026-01-26
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
     * 上传用户ID
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

    private String fileName;

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
     * 引用次数
     */
    private Integer refNum;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
