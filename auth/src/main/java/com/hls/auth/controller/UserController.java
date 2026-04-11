package com.hls.auth.controller;


import com.hls.base.R;
import com.hls.base.po.User;
import com.hls.auth.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    @GetMapping("/list")
    public List<User> list(List<Integer> ids) {
        return userService.listByIds(ids);
    }


    @PostMapping("/register")
    public R<Object> register(@RequestBody User user) {
        return userService.register(user);
    }


}
