package com.hls.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * <p>
 * 歌手信息表
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditSingerDto extends SingerDto {

    /**
     * 歌手ID（主键）
     */
    private Integer id;

}
