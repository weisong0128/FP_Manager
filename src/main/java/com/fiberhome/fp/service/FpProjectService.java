package com.fiberhome.fp.service;

import com.fiberhome.fp.pojo.FpProject;

import java.io.IOException;
import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
public interface FpProjectService {

    /**
     * 获取所有项目及地市
     * @return
     */
    List<FpProject> listProject(String pjName);


    /**
     * 上传日志  新增项目 地址
     * @param path
     * @param project
     * @param location
     * @return
     */
    boolean upload(String path,String project,String location)  throws IOException, InterruptedException;
}
