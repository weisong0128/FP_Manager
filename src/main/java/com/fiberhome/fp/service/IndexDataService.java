package com.fiberhome.fp.service;

import java.util.List;
import java.util.Map;

public interface IndexDataService {
    /**
     * 授权个数统计
     */
    Map<String, Object> authManageCount(Map<String,Object> map);

    /**
     * 根据省份查询授权个数
     */
    Map<String, Object> authManageCountByProv(Map<String,Object> map);


    /**
     * 各项目线上环境授权TOP10
     */
    List<Map<String, Object>> authManageTOP10(Map<String,Object> map);

    /***
     *FP数据库地区分布TOP5
     */
    List<Map<String, Object>> authManageTOP5(Map<String,Object> map);



    /**
     * 开放授权趋势
     */
    List<Map<String, Object>> openAuthManage(Map<String,Object> map);


    //获取全国的授权管理
    List<Map<String, Object>> getAllauthManage(Map<String,Object> map);
}
