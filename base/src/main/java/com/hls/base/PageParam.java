package com.hls.base;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParam {

    /**
     * 页码
     */
    private Integer num;

    /**
     * 每页数量
     */
    private Integer size;
}
