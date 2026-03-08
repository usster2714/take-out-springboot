package com.sky.context;

/**
 * 用户上下文，用于存储当前登录用户的ID
 */
public class UserContext {

    /**
     * 使用ThreadLocal存储当前登录用户的ID
     * ThreadLocal是线程本地存储，每个线程都有自己的副本
     */
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    /**
     * 设置当前登录用户的ID
     * @param userId 用户ID
     */
    public static void setCurrentId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前登录用户的ID
     * @return 用户ID
     */
    public static Long getCurrentId() {
        return USER_ID.get();
    }

    /**
     * 清除当前登录用户的ID
     * 在线程结束时调用，避免内存泄漏
     */
    public static void clearCurrentId() {
        USER_ID.remove();
    }
}
