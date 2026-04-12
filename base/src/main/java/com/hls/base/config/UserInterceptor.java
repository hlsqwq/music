package com.hls.base.config;

import cn.hutool.core.util.StrUtil;
import com.hls.base.po.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 1. 从 Header 获取网关传过来的 ID

        String id = request.getHeader("id");
        if (StrUtil.isBlank(id)) {
            return true;
        }
        String name = request.getHeader("name");
        String access = request.getHeader("access");
        UserInfo userInfo = new UserInfo(Integer.parseInt(id), name, access);
        UserContext.setUser(userInfo);
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