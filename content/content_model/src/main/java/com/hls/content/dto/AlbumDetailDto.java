package com.hls.content.dto;

import com.hls.base.po.Album;
import com.hls.base.po.Song;
import lombok.*;

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
