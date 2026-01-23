package com.hls.content.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hls.content.po.Category;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 分类表（歌曲/歌单/专辑分类）
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTreeDto extends Category {

    /**
     * 子分类
     */
    private List<CategoryTreeDto> children;


}
