package me.aihe.blog.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

/**
 * @author he.ai aihehe123@gmail.com
 * @date 2020/1/17 9:59
 * 使用场景：
 * 功能描述：
 */
@Component
public class SpringUtils implements ApplicationContextAware {
    static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * 根据Bean ID获取Bean
     */
    public static Object getBean(String beanId) {
        return applicationContext.getBean(beanId);
    }

    public static <T> T getBean(String beanId, Class<T> cls) {
        return applicationContext.getBean(beanId, cls);
    }

    public static <T> T getBeanByType(Class<T> cls) {
        return applicationContext.getBean(cls);
    }

    public static void publishEvent(Object event) {
        applicationContext.publishEvent(event);
    }

    public static String getProperty(String key){
        return applicationContext.getEnvironment().getProperty(key);
    }
}
