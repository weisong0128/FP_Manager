package com.fiberhome.fp.service;

import com.fiberhome.fp.pojo.UserInfo;

public interface LoginService {
    /**
     * 用户登录
     * @return
     */
    UserInfo login(String userName, String passWord);
}
