package me.aihe.blog.web;

import lombok.extern.slf4j.Slf4j;
import me.aihe.blog.config.BlogProperties;
import me.aihe.blog.pojo.Result;
import me.aihe.blog.util.FileUtils;
import me.aihe.blog.util.OssUtils;
import me.aihe.blog.util.SpringUtils;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    BlogProperties blogProperties;

    /**
     * list directory contents
     *
     * @param file
     * @return
     * @throws IOException
     */
    @GetMapping("/ls")
    public Result ls(
            @RequestParam(value = "file", required = false) String file
    ) throws IOException {
        String path = blogProperties.getPath();
        Path dir = Paths.get(path);
        if (StringUtils.isNotEmpty(file)) {
            dir = Paths.get(path, file);
        }

        if (FileUtils.notExist(dir)) {
            return Result.paramError("传入的目录有误");
        }

        List<String> catalogs = Files.list(dir)
                .map(p -> p.toFile().getName())
                .collect(Collectors.toList());

        return Result.success(catalogs);

    }


    /**
     * concatenate and print files
     *
     * @param catalog
     * @param fileName
     * @return
     */
    @GetMapping("/cat")
    public Result fileContent(
            @RequestParam("catalog") String catalog,
            @RequestParam("fileName") String fileName
    ) {
        Path path = Paths.get(blogProperties.getPath(), catalog, fileName);
        if (FileUtils.notExist(path)) {
            return Result.paramError("传入的参数有误");
        }

        try {
            String str = FileUtils.replaceJianshuPic(path);
            return Result.success(str);
        } catch (IOException e) {
            return Result.error("获取文件内容有误");
        }
    }


    /**
     * file pattern searcher
     * 简单的文件内容查找
     * @param content
     * @return
     */
    @GetMapping("/grep")
    public Result search(
            @RequestParam("content") String content
    ) {

        try {
            HashMap<String, List<String>> result = new HashMap<>(20);
            Files.list(Paths.get(blogProperties.getPath()))
                    .flatMap(listDirectory())
                    .filter(isContainsPredicate(content, result))
                    .collect(Collectors.toList())
            ;
            return Result.success(result);
        } catch (IOException e) {

            return Result.error(e.getMessage());
        }

    }

    /**
     * 在原有的代码块中抛出异常看起来不够好看
     *
     * @param keyWorld
     * @param result
     * @return
     */
    private static Predicate<Path> isContainsPredicate(String keyWorld, HashMap<String, List<String>> result) {
        return p -> {
            try {
                boolean anyMatch = grepContent(keyWorld, result, p);
                return anyMatch;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        };
    }

    /**
     * 仿Linux Grep命令，逐行匹配是否包含有想要的内容
     *
     * @param keyWorld
     * @param result
     * @param p
     * @return
     * @throws IOException
     */
    private static boolean grepContent(String keyWorld, HashMap<String, List<String>> result, Path p) throws IOException {
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
    }


    private static Function<Path, Stream<? extends Path>> listDirectory() {
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
