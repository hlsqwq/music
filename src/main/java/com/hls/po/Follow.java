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
 * 用户关注表
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("follow")
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关注ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关注者ID
     */
    private Integer userId;

    /**
     * 被关注者ID
     */
    private Integer followingId;

    /**
     * 关注时间
     */
    private LocalDateTime createTime;


}
