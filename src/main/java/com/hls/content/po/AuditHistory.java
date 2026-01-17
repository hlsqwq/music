package com.hls.content.po;

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
 * 审核历史记录表
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("audit_history")
public class AuditHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 审核历史ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 提交审核的用户ID
     */
    private Integer userId;

    /**
     * 审核媒体ID
     */
    private Integer mediaId;

    /**
     * 审核员ID（关联user表）
     */
    private Integer auditor;

    /**
     * 审核备注/失败原因
     */
    private String message;

    /**
     * 审核结果
     */
    private String result;

    /**
     * 审核时间
     */
    private LocalDateTime createTime;


}
