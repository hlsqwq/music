package com.hls.content.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "auth")
public interface UserClient {


    @GetMapping("/auth/user/list")
    List<com.hls.base.po.User> list(@RequestParam List<Integer> ids);


}
