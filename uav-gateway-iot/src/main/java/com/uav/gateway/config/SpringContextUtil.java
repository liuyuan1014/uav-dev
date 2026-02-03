package com.uav.gateway.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类
 * 用于在非Spring管理的类（如Netty Handler）中获取Spring容器中的Bean
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 获取Spring容器中的Bean
     *
     * @param clazz Bean的类型
     * @param <T>   泛型类型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            throw new RuntimeException("Spring application context is null");
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称和类型获取Bean
     *
     * @param name  Bean的名称
     * @param clazz Bean的类型
     * @param <T>   泛型类型
     * @return Bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        if (applicationContext == null) {
            throw new RuntimeException("Spring application context is null");
        }
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 获取Spring应用上下文
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}