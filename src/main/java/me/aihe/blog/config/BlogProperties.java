package me.aihe.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author he.ai aihehe123@gmail.com
 * @date 2020/1/17 9:15
 * 使用场景：
 * 功能描述：
 */
@Data
@ConfigurationProperties(prefix = "blog")
public class BlogProperties {

    /**
     * 存储所有博客内容的主目录
     */
    private String path;

    private String ossKey;

    private String ossSecret;
}
