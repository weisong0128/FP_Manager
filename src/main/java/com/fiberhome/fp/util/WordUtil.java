package com.fiberhome.fp.util;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by User
 * Create TIME 2019/9/26 11:00
 */
public class WordUtil {
    public static void main(String[] args) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("projectName", "南京");
        map.put("projectLocation", "HCZZ");
        map.put("errorResultPercent", "70");
        List<Map<String, String>> errResultList = new ArrayList<>();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("tag", String.valueOf(1));
        map1.put("alterTag", String.valueOf(1));
        map1.put("count", 1 + "");
        map1.put("exam", 1 + "");
        errResultList.add(map1);
        List<Map<String, String>> operationList = new ArrayList<>();
        HashMap<String, String> map2 = new HashMap<>();
        map2.put("dateStr", 2 + "");
        map2.put("errLevel", 2 + "");
        map2.put("count", 2 + "");
        map2.put("errInfo", 2 + "555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555");
        operationList.add(map2);
        map.put("errResults", errResultList);
        map.put("operations", operationList);
        map.put("advice", "ceshi");
        wordExport(map, "fp_template.ftl", "D:\\test", "测试.xml", "");
    }

    static Logger logging = LoggerFactory.getLogger(WordUtil.class);

    private static String encodedType = "utf-8";

    /**
     * @description:导出word报表
     * @Param:
     * @Return:
     * @Auth:User on 2019/9/26 15:14
     */
    public static void wordExport(Map<String, Object> hashMap, String templateName, String outFilePath, String outFileName, String templateRootPath) {

        Writer out = null;
        try {
            //创建配置实例
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
            //配置编码
            configuration.setDefaultEncoding(encodedType);
            //配置生成模板位置
            String templatePath = null;

            FileTemplateLoader fileTemplateLoader = null;
            try {
                templatePath = WordUtil.class.getClassLoader().getResource("").getPath();
                fileTemplateLoader = new FileTemplateLoader(new File(templatePath));
            } catch (IOException e) {
                templatePath = templateRootPath;
                fileTemplateLoader = new FileTemplateLoader(new File(templatePath));
            }
            configuration.setTemplateLoader(fileTemplateLoader);
            String path = File.separator + "template" + File.separator + templateName;
            //获取模板
            Template template = configuration.getTemplate(path, encodedType);
            //检查文件夹
            File outFile = new File(outFilePath + File.separator + outFileName);
            FileUtil.creatDir(outFile.getParent());
            //将模板和数据模型合并成文件
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), encodedType));
            template.process(hashMap, out);
        } catch (IOException | TemplateException e) {
            logging.error(e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.flush();
                } catch (IOException e) {
                    logging.error(e.getMessage());
                }
                FileUtil.closeStream(out);
            }
        }

    }
}
