package me.aihe.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author he.ai aihehe123@gmail.com
 * @date 2020/1/17 9:16
 * 使用场景：
 * 功能描述：
 */
@EnableConfigurationProperties(BlogProperties.class)
@Configuration
public class BlogConfig {
    @Autowired
    BlogProperties blogProperties;
}
