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
 * 用户信息表
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 性别
     */
    private String sex;

    /**
     * 用户唯一标识（unionid）
     */
    private String account;

    /**
     * 密码（建议加密存储）
     */
    private String passwd;

    /**
     * 关注数
     */
    private Integer followNum;

    /**
     * 粉丝数
     */
    private Integer fansNum;

    /**
     * 默认“我喜欢”歌单ID
     */
    private Integer songListId;

    /**
     * 权限：普通会员/副管理员/管理员
     */
    private String access;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
