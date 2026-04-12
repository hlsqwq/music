package com.hls.base.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
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
