package me.aihe.blog.util;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
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

    @Test
    public void downloadImage(){

        Path target = null;
        try {
//            String url = "https://upload-images.jianshu.io/upload_images/426671-f9498c34db60ffbd.jpg";
            String url = "https://aihes.oss-cn-hangzhou.aliyuncs.com/jianshu/426671-720a65fe7f07af3a.jpg";
            System.out.println(System.getProperty("java.io.tmpdir"));
            target = Paths.get(System.getProperty("java.io.tmpdir") + OssUtils.getUrlLastPath(url));
            long copy = Files.copy(new URL(url).openStream(), target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(copy);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}