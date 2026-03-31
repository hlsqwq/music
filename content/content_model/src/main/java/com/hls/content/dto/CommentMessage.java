package com.hls.content.dto;


import com.hls.base.utils.RedisKeys;
import com.hls.content.vo.CommentVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentMessage {

    /**
     * mv 或者 song 的id
     */
    Integer id;
    RedisKeys.TableType type;
    List<CommentVo> list;
}
