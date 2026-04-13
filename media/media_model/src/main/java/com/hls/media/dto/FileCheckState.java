package com.hls.media.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileCheckState implements Serializable {
    private String state;
    private Integer mediaId;
    private String md5;
    private String mediaUrl;
}
