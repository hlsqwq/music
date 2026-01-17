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
 * 用户粉丝表
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fans")
public class Fans implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 粉丝ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 被粉丝ID
     */
    private Integer userId;

    /**
     * 粉丝ID
     */
    private Integer fansId;

    /**
     * 关注时间
     */
    private LocalDateTime createTime;


}
