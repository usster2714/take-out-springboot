package com.sky.aspect;

import com.sky.annotation.AutoFill;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 公共字段自动填充切面类
 */
@Aspect
@Component
public class AutoFillAspect {

    /**
     * 切入点：拦截所有带有AutoFill注解的方法
     */
    @Pointcut("@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {}

    /**
     * 前置通知：在方法执行前为公共字段赋值
     */
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取方法上的AutoFill注解
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        // 获取操作类型
        AutoFill.OperationType operationType = autoFill.value();

        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        // 假设第一个参数是实体对象
        Object entity = args[0];

        // 获取当前登录用户ID
        Long currentUserId = com.sky.context.UserContext.getCurrentId();
        // 如果没有获取到用户ID（比如非登录状态下的操作），使用默认值1L
        if (currentUserId == null) {
            currentUserId = 1L;
        }

        try {
            // 获取实体类的所有字段
            Field[] fields = entity.getClass().getDeclaredFields();

            if (AutoFill.OperationType.INSERT.equals(operationType)) {
                // 插入操作，填充创建时间、更新时间、创建用户、更新用户
                setFieldValue(entity, "createTime", LocalDateTime.now());
                setFieldValue(entity, "updateTime", LocalDateTime.now());
                setFieldValue(entity, "createUser", currentUserId);
                setFieldValue(entity, "updateUser", currentUserId);
            } else if (AutoFill.OperationType.UPDATE.equals(operationType)) {
                // 更新操作，填充更新时间、更新用户
                setFieldValue(entity, "updateTime", LocalDateTime.now());
                setFieldValue(entity, "updateUser", currentUserId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置对象的字段值
     */
    private void setFieldValue(Object obj, String fieldName, Object value) {
        if (obj == null) {
            return;
        }

        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(obj, value);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
