package com.hls.sms.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.hls.base.R;
import com.hls.base.utils.RedisKeys;
import com.hls.sms.vo.CheckCodeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final StringRedisTemplate redisTemplate;

    /**
     * 生成验证码
     */
    @GetMapping("/create")
    public R<CheckCodeVo> create() {
        // 1. 使用 Hutool 生成线段干扰验证码 (宽, 高, 字符数, 干扰线数)
        String uuid = UUID.randomUUID().toString();
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 10);

        // 2. 获取文字内容并存入 Redis (过期时间 2 分钟)
        String code = captcha.getCode();
        redisTemplate.opsForValue().set("captcha:" + uuid, code, 2, TimeUnit.MINUTES);

        // 3. 返回 Base64 给前端
        return R.success(new CheckCodeVo(uuid, captcha.getImageBase64Data()));
    }

    /**
     * 内部校验接口（供 Auth 服务 Feign 调用）
     */
    @GetMapping("/validate")
    public boolean validate(@RequestParam String uuid, @RequestParam String code) {
        String key = "captcha:" + uuid;
        String redisCode = redisTemplate.opsForValue().get(key);
        if (StrUtil.equalsIgnoreCase(redisCode, code)) {
            redisTemplate.delete(key); // 校验成功立即删除
            return true;
        }
        return false;
    }
}