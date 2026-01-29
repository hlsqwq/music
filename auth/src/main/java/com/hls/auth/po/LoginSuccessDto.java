package com.hls.auth.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessDto {

    private Integer code;
    private String message;
    private Long expires;
    private String token;
    private String token_type;
}
