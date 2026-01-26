package com.hls.base.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.Serializable;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler implements Serializable {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exceptionHandler(Exception e) {
        log.error("异常是：{}",e.getMessage(), e);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

    @ExceptionHandler(MusicException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exceptionHandler(MusicException e) {
        log.error("异常是：{}",e.getMessage(), e);
        return new RestErrorResponse(e.getMessage());
    }

}
