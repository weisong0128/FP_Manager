package com.fiberhome.fp.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
public class StrUtil {

    public static List<String> json2List(String json){
        JSONArray jsonArray = JSON.parseArray(json);
        return jsonArray.toJavaList(String.class);
    }



    public static String assemblyProject(String project,List<String> locations){
        StringBuilder content = new StringBuilder();
        content.append(project + ",");
        if (locations != null && locations.size()>0){
            for (String location:locations){
                content.append(location + " ");
            }
        }
        return content.toString();
    }


}
