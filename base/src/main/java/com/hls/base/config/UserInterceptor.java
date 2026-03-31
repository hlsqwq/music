package com.hls.base.config;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 1. 从 Header 获取网关传过来的 ID
        String userId = request.getHeader("UserId");
        if (StrUtil.isNotBlank(userId)) {
            // 2. 存入 ThreadLocal
            UserContext.setUser(Integer.valueOf(userId));
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        // 3. 请求结束，清理内存
        UserContext.removeUser();
    }
}