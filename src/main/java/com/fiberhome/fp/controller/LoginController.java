package com.fiberhome.fp.controller;

import com.fiberhome.fp.pojo.UserInfo;
import com.fiberhome.fp.service.impl.LoginServiceImpl;
import com.fiberhome.fp.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Api(value = "登录",description = "用户登录")
@Controller
@RequestMapping("/login/")
@ResponseBody
public class LoginController {
    @Autowired
    LoginServiceImpl loginService;

    @ApiOperation(value="登录方法",notes = "用户登录")
    @GetMapping("login")
    public Response login(String userName , String password) throws ExecutionException, InterruptedException {
       UserInfo userInfo = new UserInfo();
        userInfo.setUserName(userName);
        userInfo.setUserPassword(password);
        int count = loginService.login(userInfo.getUserName(), userInfo.getUserPassword());
        return Response.ok(count);
    }
}
