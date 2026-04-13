package com.hls.media.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaVo {
    private String state;
    private String signature;
    private Integer mediaId;
    private String mediaUrl;


    public static MediaVo findIt(Integer mediaId, String mediaUrl) {
        return new MediaVo()
                .setState("ok")
                .setMediaId(mediaId)
                .setMediaUrl(mediaUrl);
    }

    public static MediaVo upload(String signature) {
        return new MediaVo()
                .setState("ing")
                .setSignature(signature);
    }

}
