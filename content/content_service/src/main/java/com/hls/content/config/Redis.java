package com.hls.content.config;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Redis {
    String type() default "get";
    String key() default "";
}
