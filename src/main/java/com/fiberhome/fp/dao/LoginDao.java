package com.fiberhome.fp.dao;

import com.fiberhome.fp.pojo.UserInfo;

/**
 * 登录接口
 */
public interface LoginDao {
    /**
     * 用户登录
     * @return
     */
    UserInfo login(String userName, String passWord);

}
