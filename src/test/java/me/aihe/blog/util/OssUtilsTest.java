package me.aihe.blog.util;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author he.ai aihehe123@gmail.com
 * @date 2020/1/17 10:35
 * 使用场景：
 * 功能描述：
 */
class OssUtilsTest {

    @Test
    void uploadUrl() throws URISyntaxException, IOException {
        String accessKey = "LTAI4FxZcQciXhK7WYwUwCqW";
        String accessSecret = "wB2uENsPZKW5IXTBMU6jEZHaYIdyQH";
        OSSClient ossClient = new OSSClient("http://oss-cn-hangzhou.aliyuncs.com", accessKey, accessSecret);

        String bucketName = "aihes";
        String prefix = "jianshu/";

        String url = "https://upload-images.jianshu.io/upload_images/426671-142d4348b8261a4e.png";
        String objectKey = prefix + OssUtils.getUrlLastPath(url);

        ossClient.putObject(bucketName, objectKey, new URL(url).openStream());

        ossClient.shutdown();

    }

    @Test
    void writeTest() throws IOException {
        String path = "D:\\code\\blog\\test";
        Files.write(Paths.get(path), LocalDateTime.now().toString().getBytes(), StandardOpenOption.WRITE);
    }
}