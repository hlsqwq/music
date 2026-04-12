package com.hls.base.config;

import com.hls.base.po.UserInfo;

public class UserContext {
    private static final ThreadLocal<UserInfo> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(UserInfo userInfo) {
        USER_THREAD_LOCAL.set(userInfo);
    }

    public static UserInfo getUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static void removeUser() {
        USER_THREAD_LOCAL.remove(); // 必须手动移除，防止线程池内存泄漏
    }
}