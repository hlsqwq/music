package com.hls.po;

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
 * @since 2026-01-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("media")
public class Media implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 媒体ID（主键）
     */
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
     * 头像文件URL
     */
    private String avatar;

    /**
     * 音频文件URL
     */
    private String music;

    /**
     * MV文件URL
     */
    private String mv;

    /**
     * 媒体审核状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
