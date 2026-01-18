package com.hls;


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


    public static R success(){
        return new R(200,"ok",null);
    }


    public static R failure(){
        return new R(500,"发生未知错误，请联系管理员",null);
    }
    public static<T> R<T> failure(T data){
        return new R(500,"ok",data);
    }
    public static<T> R<T> failure(String message,T data){
        return new R(500,message,data);
    }
    public static<T> R<T> failure(int code,String message,T data){
        return new R(code,message,data);
    }

}
