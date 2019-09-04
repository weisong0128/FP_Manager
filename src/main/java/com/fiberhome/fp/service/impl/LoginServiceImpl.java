package com.fiberhome.fp.service.impl;

import com.fiberhome.fp.dao.LoginDao;
import com.fiberhome.fp.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    LoginDao loginDao;
    @Override
    public int login(String userName, String passWord) {
        return loginDao.login(userName,passWord);
    }
}
