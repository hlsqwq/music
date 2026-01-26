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
 * 媒体临时存储
 * </p>
 *
 * @author hls
 * @since 2026-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("media_temp")
public class MediaTemp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    /**
     * minio,temp存储路径
     */
    private String path;

    private String url;

    /**
     * 文件名
     */
    private String fileName;

    private String md5;

    /**
     * 文件大小
     */
    private Integer size;

    /**
     * 重试次数
     */
    private Integer retry;

    private LocalDateTime createTime;


}
