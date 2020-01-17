package me.aihe.blog.web;

import me.aihe.blog.config.BlogProperties;
import me.aihe.blog.pojo.Result;
import me.aihe.blog.util.OssUtils;
import me.aihe.blog.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author he.ai aihehe123@gmail.com
 * @date 2020/1/17 9:17
 * 使用场景：
 * 功能描述：
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    BlogProperties blogProperties;

    @GetMapping("/catalog")
    public Result catalog() {
        try {
            String path = blogProperties.getPath();
            List<String> catalogs = Files.list(Paths.get(path))
                    .map(p -> p.toFile().getName())
                    .collect(Collectors.toList());
            return Result.success(catalogs);
        } catch (IOException e) {
            return Result.error(e.getMessage() + "获取目录出错");
        }
    }

    @GetMapping("/fileList")
    public Result fileList(
            @RequestParam("catalog") String catalog
    ) {
        try {
            String path = blogProperties.getPath();
            Path abPath = Paths.get(path, catalog);
            boolean exists = abPath.toFile().exists();
            if (!exists) {
                return Result.paramError("传入的目录有误");
            }

            List<String> catalogs = Files.list(abPath)
                    .map(p -> p.toFile().getName())
                    .collect(Collectors.toList());
            return Result.success(catalogs);
        } catch (IOException e) {
            return Result.error(e.getMessage() + "获取目录内容出错");
        }
    }

    static Pattern picPattern = Pattern.compile("https?://upload-images.jianshu.io/upload_images/\\S+.(jpg|jepg|png)");

    @GetMapping("/fileContent")
    public Result fileContent(
            @RequestParam("catalog") String catalog,
            @RequestParam("fileName") String fileName
    ) {
        String path = blogProperties.getPath();
        Path abPath = Paths.get(path, catalog, fileName);
        boolean exists = abPath.toFile().exists();
        if (!exists) {
            return Result.paramError("传入的参数有误");
        }

        try {
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
            return Result.success(str);
        } catch (IOException e) {
            return Result.error("获取文件内容有误");
        }
    }

    @GetMapping("/search")
    public Result search(
            @RequestParam("content") String content
    ) {
//        grep -r -i "学习"  ./
        // 多线程进行过滤结果
        // 获取所有目录
        // 获取所有目录下的文件内容
        // 对内容进行过滤
        // 返回结果

        try {
            HashMap<String, List<String>> result = new HashMap<>(20);
            Files.list(Paths.get(blogProperties.getPath()))
                    .flatMap(listDireactory())
                    .filter(isContainsPredicate(content, result))
                    .collect(Collectors.toList())
            ;
            return Result.success(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.error("未搜索到有效内容");
    }

    private static Predicate<Path> isContainsPredicate(String keyWorld, HashMap<String, List<String>> result) {
        return p -> {
            try {
                return Files.readAllLines(p)
                        .stream()
                        .anyMatch(s -> {
                            boolean contains = s.contains(keyWorld);
                            if (contains) {
                                String key = p.toString()
                                        .replace(SpringUtils.getBeanByType(BlogProperties.class).getPath(), "")
                                        // 去除Windows前缀
                                        .replaceAll("\\\\", "->")
                                        // 去除Linux前缀
                                        .replaceAll("/", "->");
                                result.computeIfAbsent(key, k -> new LinkedList<>());
                                result.get(key).add(s);
                            }
                            return contains;
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        };
    }

    private static Function<Path, Stream<? extends Path>> listDireactory() {
        return p -> {
            try {
                return Files.list(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        };
    }
}
