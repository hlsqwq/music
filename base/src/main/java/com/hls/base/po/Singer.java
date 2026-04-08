package com.hls.base.po;

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
 * 歌手信息表
 * </p>
 *
 * @author hls
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("singer")
public class Singer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 歌手ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 歌手姓名
     */
    private String name;

    /**
     * 分类
     */
    private Integer categoryId;

    private Integer avatarId;

    /**
     * 封面
     */
    private String avatarUrl;

    /**
     * 发布歌曲数量
     */
    private Integer songNum;

    /**
     * 发布专辑数量
     */
    private Integer albumNum;

    /**
     * 发布MV数量
     */
    private Integer mvNum;

    /**
     * 粉丝数
     */
    private Long fansNum;

    /**
     * 热度
     */
    private Long hot;

    /**
     * 长简介id
     */
    private Integer introductionId;

    /**
     * 歌手简介
     */
    private String introduction;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
