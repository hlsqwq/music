package com.hls.auth.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthParams {

    private String username;
    private String password;
    private String mail;
    private String method;
    private String checkCode;
    private String checkCodeKey;
}
