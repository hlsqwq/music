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
 * 审核申请表
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("audit")
public class Audit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 审核ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 提交审核的用户ID
     */
    private Integer userId;

    /**
     * 待审核媒体ID
     */
    private Integer mediaId;

    /**
     * 提交审核时间
     */
    private LocalDateTime createTime;


}
