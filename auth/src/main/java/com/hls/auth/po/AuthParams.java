package com.hls.auth.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthParams {

    /**
     * 账号
     */
    private String account;
    /**
     * 邮箱登录
     */
    private String mail;

    /**
     * 密码
     */
    private String password;

    /**
     * 登录的方式 passwd
     */
    private String method;

    /**
     * 图形验证码
     */
    private String checkCode;

    /**
     * 图形验证码redis key
     */
    private String checkCodeKey;
}
