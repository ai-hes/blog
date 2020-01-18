package me.aihe.blog.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
import me.aihe.blog.BlogException;
import me.aihe.blog.config.BlogProperties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * @author he.ai aihehe123@gmail.com
 * @date 2020/1/17 9:52
 * 使用场景：
 * 功能描述：
 */
public class OssUtils {

    static OSSClient ossClient;

    static String prefix = "jianshu/";

    static String bucketName = "aihes";

    static String keyFormat = "https://aihes.oss-cn-hangzhou.aliyuncs.com/%s";

    public static void main(String[] args) {
        Properties properties = System.getProperties();
        for (Object o : properties.keySet()) {
            System.out.println(o.toString() + properties.get(o));
        }
    }

    private static OSSClient getOssClient() {
        if (ossClient != null) {
            return ossClient;
        }
        synchronized (OssUtils.class) {
            if (ossClient == null) {
                BlogProperties blogProperties = SpringUtils.getBeanByType(BlogProperties.class);
                String accessKey = blogProperties.getOssKey();
                String accessSecret = blogProperties.getOssSecret();
                ossClient = new OSSClient("http://oss-cn-hangzhou.aliyuncs.com", accessKey, accessSecret);
                return ossClient;
            }
        }

        return ossClient;
    }

    public static String uploadUrl(String url) {
        try {

            String objectKey = prefix + getUrlLastPath(url);
            Path target = Paths.get(System.getProperty("java.io.tmpdir") + getUrlLastPath(url));
            copyFiletoLocal(url, target);
            if (!isExist(objectKey)){
                PutObjectResult putObjectResult = getOssClient().putObject(bucketName, objectKey, target.toFile());
            }
            return String.format(keyFormat, objectKey);
        } catch (IOException | URISyntaxException e) {
            throw new BlogException("上传OSS异常" + e.getMessage());
        }
    }

    /**
     * 判断对象是否存在
     * @param objectKey
     * @return
     */
    private static boolean isExist(String objectKey) {
        boolean exist = getOssClient().doesObjectExist(bucketName, objectKey);
        return exist;
    }

    /**
     * 一些图片可能只下载部分，导致无法查看
     * @param url
     * @param target
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void copyFiletoLocal(String url, Path target) throws IOException, URISyntaxException {
        for (int i = 0; i < 3; i++) {
            InputStream inputStream = new URL(url).openStream();
            long fileLength = Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            if (fileLength > 1000) {
                break;
            }
        }
    }

    public static String getUrlLastPath(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String path = uri.getPath();
        int lastSlashIndex = path.lastIndexOf("/");
        path = path.substring(lastSlashIndex + 1);
        return path;
    }
}
