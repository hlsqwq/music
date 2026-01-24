package com.hls.content.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hls.content.po.Album;
import com.hls.content.po.Song;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 专辑信息表
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlbumDetailDto extends Album {


    /**
     * 专辑中包含的歌曲
     */
    List<Song> songs;
}
