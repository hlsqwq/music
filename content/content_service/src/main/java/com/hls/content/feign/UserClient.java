package com.hls.content.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient
public interface UserClient {


    @GetMapping("/auth/user/list")
    List<com.hls.base.po.User> list(List<Integer>ids);


}
