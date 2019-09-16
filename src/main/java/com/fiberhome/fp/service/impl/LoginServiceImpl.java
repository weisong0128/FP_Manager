package com.fiberhome.fp.service.impl;

import com.fiberhome.fp.dao.LoginDao;
import com.fiberhome.fp.pojo.UserInfo;
import com.fiberhome.fp.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.ContextExposingHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    LoginDao loginDao;
    @Override
    public UserInfo login(String userName, String passWord) {
        UserInfo userInfo = loginDao.login(userName, passWord);
        return userInfo;
    }
}
