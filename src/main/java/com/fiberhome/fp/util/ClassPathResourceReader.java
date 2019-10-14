package com.fiberhome.fp.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.stream.Collectors;

/**
 * 用于读取springboot jar配置文件的工具类
 * Create by User
 * Create TIME 2019/10/12 16:11
 */
public class ClassPathResourceReader {

    /**
     * @description:返回文件的内容
     * @Param:[path]
     * @Return:java.lang.String
     * @Auth:User on 2019/10/12 16:12
     */
    public String getContent(String resourceLocation) {
        ClassPathResource resource = new ClassPathResource(resourceLocation);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String content = reader.lines().collect(Collectors.joining("\n"));
            return content;
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            FileUtil.closeStream(reader);
        }
    }

   /* public String getResourceFilePath(String resourceLocation) {
        ClassPathResource resource = new ClassPathResource(resourceLocation);
        InputStream inputStream = resource.getInputStream();
        File.createTempFile("xx","rr");
        FileCopyUtils;
    }*/
}
