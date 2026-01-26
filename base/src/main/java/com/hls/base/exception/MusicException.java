package com.hls.base.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicException extends RuntimeException {
    private String errMessage;

    public static void cast(String errMessage) {
        throw new MusicException(errMessage);
    }

    public static void cast(CommonError error) {
        throw new MusicException(error.getErrMessage());
    }
}
