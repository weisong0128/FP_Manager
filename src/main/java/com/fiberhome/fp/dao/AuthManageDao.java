package com.fiberhome.fp.dao;

import com.fiberhome.fp.pojo.AuthManage;
import com.fiberhome.fp.util.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;

/***
 * 授权管理接口
 */
public interface AuthManageDao {
    /***
     * 新增授权
     */
    String createAuthManage(AuthManage authManage);

    /**
     * 获取所有授权信息（包括条件查询）
     */

    List<AuthManage> getAllAuthManage(Page page,AuthManage authManage);

    /**
    * 修改和删除授权管理
    */
    int  updateAndDelete(Map<String ,Object> parames);

    /**
     * 获取所有的安装地市
     */

    List<Map<String,Object>> getAllCities(String pjName);

    /**
     * 获取所有的项目名称
     * @return
     */
    List<Map<String,Object>> getAllPjName();
}
