package com.hls.auth.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "sms")
public interface SmsClient {

    /**
     *
     * @param uuid   uuid
     * @param code  captcha
     * @return
     */
    @GetMapping("/sms/captcha/validate")
    boolean captchaValidate(@RequestParam String uuid,@RequestParam String code);


}
