package com.hls.auth.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient
public interface SmsClient {



    //todo 带使用
    /**
     *
     * @param key   uuid
     * @param code  captcha
     * @return
     */
    @GetMapping("/sms/captcha/validate")
    boolean captchaValidate(String key,String code);


}
