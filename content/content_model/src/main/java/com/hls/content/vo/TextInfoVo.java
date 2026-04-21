package com.hls.content.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
public class TextInfoVo {
    /**
     * 存入的长文本id
     */
    private Integer id;

    /**
     * 需要保存的简要内容
     */
    private String text;
}
