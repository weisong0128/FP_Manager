package com.fiberhome.fp.service.impl;

import com.fiberhome.fp.dao.AuthManageDao;
import com.fiberhome.fp.pojo.AuthManage;
import com.fiberhome.fp.service.AuthManageService;
import com.fiberhome.fp.util.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class AuthManageServiceImpl implements AuthManageService {
    @Resource
    AuthManageDao authManageDao;
    @Override
    public String createAuthManage(AuthManage authManage) {
        return authManageDao.createAuthManage(authManage);
    }

    @Override
    public List<AuthManage> getAllAuthManage(Page page, AuthManage authManage) {
        return authManageDao.getAllAuthManage(page,authManage);
    }

    @Override
    public int updateAndDelete(Map<String, Object> parames) {
        return authManageDao.updateAndDelete(parames);
    }

    @Override
    public List<Map<String, Object>> getAllCities() {
        return authManageDao.getAllCities();
    }

    @Override
    public List<Map<String, Object>> getAllPjName() {
        return authManageDao.getAllPjName();
    }

}
