package com.hls.base;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class R<T> {
    private int code;
    private String msg;
    private T data;


    public static R<Object> success() {
        return new R<>(200, "ok", null);
    }

    public static <T> R<T> success(T data) {
        return new R<>(200, "ok", data);
    }

    public static R<Object> failure() {
        return failure(600, "发生未知错误，请联系管理员", null);
    }

    public static <T> R<T> failure(String message) {
        return failure(600, message, null);
    }

    public static <T> R<T> failure(String message, T data) {
        return failure(600, message, data);
    }

    public static <T> R<T> failure(int code, String message, T data) {
        return new R<>(code, message, data);
    }

}
