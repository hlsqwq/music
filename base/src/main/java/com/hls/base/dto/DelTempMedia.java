package com.hls.base.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelTempMedia {
    Integer userMediaId;
    String md5;
    String bucketName;
    String filePath;
}
