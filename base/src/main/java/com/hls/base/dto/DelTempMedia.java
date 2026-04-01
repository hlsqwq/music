package com.hls.base.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelTempMedia {
    String key;
    String bucketName;
    String filePath;
}
