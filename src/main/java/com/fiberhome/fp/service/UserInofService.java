package com.fiberhome.fp.service;

import com.fiberhome.fp.pojo.UserInfo;
import com.fiberhome.fp.util.Page;

import java.util.List;
import java.util.Map;

public interface UserInofService {
    /***
     * 创建用户
     * @param user  用户实体
     */
    String createUser(UserInfo user);

    /**
     * 修改和删除用户信息
     * @param userInfo
     * @return
     */
    int updateUserInfo(UserInfo userInfo);

    /**
     * 获取所有的用户信息
     */
    List<UserInfo> getAllUserInfo(Page page,UserInfo userInfo);

    /**
     * 根据uuid获取单个用户
     */
    UserInfo getUserInfoByUuid(String uuid);

    /***
     * 删除，启用，禁用
     */

    int updateState(String state, Map<String,Object> parames);
}
