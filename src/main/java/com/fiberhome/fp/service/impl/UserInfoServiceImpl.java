package com.fiberhome.fp.service.impl;

import com.fiberhome.fp.dao.UserInfoDao;
import com.fiberhome.fp.pojo.UserInfo;
import com.fiberhome.fp.service.UserInofService;
import com.fiberhome.fp.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl implements UserInofService {
    @Autowired
    UserInfoDao userInfoDao;

    @Override
    public String createUser(UserInfo user) {
        return userInfoDao.createUser(user);
    }

    @Override
    public int updateUserInfo(UserInfo userInfo) {
        return userInfoDao.updateUserInfo(userInfo);
    }

    @Override
    public List<UserInfo> getAllUserInfo(Page page,UserInfo userInfo) {
        return userInfoDao.getAllUserInfo(page,userInfo);
    }

    @Override
    public UserInfo getUserInfoByUuid(String uuid) {
        return userInfoDao.getUserInfoByUuid(uuid);
    }

    @Override
    public UserInfo getUserInfoByUserName(String userName) {
        return userInfoDao.getUserInfoByUserName(userName);
    }

    @Override
    public int updateState(String state, Map<String,Object> parames) {
        return userInfoDao.updateState(state,parames);
    }
}
