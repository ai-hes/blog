package me.aihe.blog.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : aihe
 * @date : 2020-01-18
 * @Description:
 */
public class FileUtils {

    /**
     * 简书图片匹配Pattern
     */
    private static Pattern picPattern = Pattern.compile("https?://upload-images.jianshu.io/upload_images/\\S+.(jpg|jepg|png)");

    /**
     * 将文中简书的图片提前后，上传到OSS，并且重新写入内容
     *
     * @param abPath
     * @return
     * @throws IOException
     */
    public static String replaceJianshuPic(Path abPath) throws IOException {
        byte[] allBytes = Files.readAllBytes(abPath);
        String str = new String(allBytes);

        HashMap<String, String> map = new HashMap<>(20);

        Matcher matcher = picPattern.matcher(str);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String url = str.substring(start, end);
            // 上传url
            String result = OssUtils.uploadUrl(url);
            map.put(url, result);
        }

        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            str = str.replace(stringStringEntry.getKey(), stringStringEntry.getValue());
        }
        // 在需要进行替换的时候重新写入文件
        if (map.keySet().size() > 0) {
            Files.write(abPath, str.getBytes(), StandardOpenOption.WRITE);
        }
        return str;
    }

    /**
     * 判断某个路径在电脑中是否存在
     * @param path
     * @return
     */
    public static boolean exist(Path path){
        return path.toFile().exists();
    }

    /**
     * 判断某个目录不存在
     * @param path
     * @return
     */
    public static boolean notExist(Path path){
        return !exist(path);
    }


}
