package com.fiberhome.fp.controller;

import com.fiberhome.fp.pojo.UserInfo;
import com.fiberhome.fp.service.impl.LoginServiceImpl;
import com.fiberhome.fp.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public Response login(UserInfo userInfo) {
        int count = loginService.login(userInfo.getUserName(),userInfo.getUserPassword());
        return Response.ok(count);
    }
}
