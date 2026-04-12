package com.hls.base.exception;


import com.hls.base.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.Serializable;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler implements Serializable {


    @ExceptionHandler(MusicException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<String> exceptionHandler(MusicException e) {
        log.error("异常是：{}", e.getMessage(), e);
        return R.failure(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<String> exceptionHandler(Exception e) {
        log.error("异常是：{}", e.getMessage(), e);
        return R.failure(CommonError.UNKOWN_ERROR.getErrMessage());
    }
}
