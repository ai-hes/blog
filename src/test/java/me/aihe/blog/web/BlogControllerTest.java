package me.aihe.blog.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author he.ai aihehe123@gmail.com
 * @date 2020/1/17 9:22
 * 使用场景：
 * 功能描述：
 */
//@SpringBootTest
class BlogControllerTest {

    RestTemplate restTemplate;

    String prefix = "http://127.0.0.1:8082/blog/";

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

    }

    @Test
    public void catelogTest() throws IOException {
        String path = "C:\\Users\\wb-ah558847\\Desktop\\学习\\user-426671-1579159984";
        List<String> catelogs = Files.list(Paths.get(path))
                .map(p -> p.toFile().getName())
                .collect(Collectors.toList());
        System.out.println(catelogs);
    }

    @Test
    void catalog() {
        ResponseEntity<String> forEntity = restTemplate.getForEntity(prefix + "catalog", String.class);
        System.out.println(forEntity.getBody());
    }


    @Test
    void testCatalog() {
    }

    @Test
    void fileList() {
        String catalog = "007行动";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(prefix + "fileList?catalog=" + catalog, String.class);
        System.out.println(forEntity.getBody());
    }

    @Test
    void fileContent() {
        String catalog = "007行动";
        String fileName = "100公里的骑行.md";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(prefix + "fileContent?catalog=" + catalog + "&fileName=" + fileName, String.class);
        System.out.println(forEntity.getBody());
    }

    @Test
    void search() {
        String content = "山路";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(prefix + "search?content=" + content, String.class);
        System.out.println(forEntity.getBody());
    }
}