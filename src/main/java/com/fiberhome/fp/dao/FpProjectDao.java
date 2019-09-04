package com.fiberhome.fp.dao;

import com.fiberhome.fp.pojo.FpProject;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
public interface FpProjectDao {


    /**
     * 获取项目和项目地点集合
     * @return
     */
    List<FpProject> listProject(String pjName);
}
