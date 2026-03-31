package com.hls.base.config;

public class UserContext {
    private static final ThreadLocal<Integer> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(Integer userId) {
        USER_THREAD_LOCAL.set(userId);
    }

    public static Integer getUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static void removeUser() {
        USER_THREAD_LOCAL.remove(); // 必须手动移除，防止线程池内存泄漏
    }
}