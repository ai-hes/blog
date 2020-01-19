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

import static me.aihe.blog.constant.OssConstants.*;

/**
 * @author he.ai aihehe123@gmail.com
 * @date 2020/1/17 9:52
 * 使用场景：
 * 功能描述：
 */
public class OssUtils {

    private static OSSClient ossClient;

    /**
     * 完整的OSS文件地址
     */
    private static String keyFormat = "https://aihes.oss-cn-hangzhou.aliyuncs.com/%s";

    /**
     * 重试下载网络文件次数
     */
    private static int retryCount = 3;


    public static OSSClient getOssClient() {
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
        return doUploadUrl(url, false);
    }

    public static String uploadUrl(String url, boolean force) {
        return doUploadUrl(url, force);
    }


    private static String doUploadUrl(String url, boolean force) {
        try {

            String objectKey = generateOssObjectKey(url);

            Path target = Paths.get(System.getProperty("java.io.tmpdir") + getUrlLastPath(url));
            copyFiletoLocal(url, target);
            if (!isExist(objectKey) || force) {
                PutObjectResult putObjectResult = getOssClient().putObject(BUCKET_NAME, objectKey, target.toFile());
            }
            return String.format(keyFormat, objectKey);
        } catch (IOException | URISyntaxException e) {
            throw new BlogException("上传OSS异常" + e.getMessage());
        }
    }

    /**
     * 生成上传到OSS存储空间时的文件key
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    private static String generateOssObjectKey(String url) throws URISyntaxException {
        return PREFIX + getUrlLastPath(url);
    }

    /**
     * 判断对象是否存在
     *
     * @param objectKey
     * @return
     */
    private static boolean isExist(String objectKey) {
        boolean exist = getOssClient().doesObjectExist(BUCKET_NAME, objectKey);
        return exist;
    }

    /**
     * 一些图片可能只下载部分，导致无法查看
     *
     * @param url
     * @param target
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void copyFiletoLocal(String url, Path target) throws IOException, URISyntaxException {
        for (int i = 0; i < retryCount; i++) {
            InputStream inputStream = new URL(url).openStream();
            long fileLength = Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            if (fileLength > 1000) {
                break;
            }
        }
    }

    /**
     * 获取url地址中路径最后的文件名
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static String getUrlLastPath(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String path = uri.getPath();
        int lastSlashIndex = path.lastIndexOf("/");
        path = path.substring(lastSlashIndex + 1);
        return path;
    }
}
