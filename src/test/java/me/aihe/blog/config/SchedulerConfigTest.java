package me.aihe.blog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : aihe
 * @date : 2020-01-19
 * @Description:
 */
@SpringBootTest
class SchedulerConfigTest {

    @Autowired
    SchedulerConfig schedulerConfig;

    @Test
    void reUpload() {
        schedulerConfig.reUpload();
    }
}