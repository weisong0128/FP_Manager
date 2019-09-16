package com.fiberhome.fp.controller;

import com.fiberhome.fp.pojo.UserInfo;
import com.fiberhome.fp.service.impl.LoginServiceImpl;
import com.fiberhome.fp.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Api(value = "登录",description = "用户登录")
@Controller
@RequestMapping("/fplogin/")
@ResponseBody
public class LoginController {
    @Autowired
    LoginServiceImpl loginService;

    @ApiOperation(value="登录方法",notes = "用户登录")
    @RequestMapping(value = "fplogin",method = RequestMethod.POST)
    public Response login(UserInfo userInfo, HttpServletRequest request) {
        UserInfo userInfo1 = loginService.login(userInfo.getUserName(),userInfo.getUserPassword());
        HttpSession session = request.getSession();
        session.setAttribute(userInfo1.getUuid(),userInfo1);
        Map map = new HashMap();
        if (userInfo1!=null){
            map.put("uuid",userInfo1.getUuid());
            map.put("userRole",userInfo1.getUserRole());
            return Response.ok(map);
        }
     return Response.error(null);
    }

    @GetMapping("quit")
    @ApiOperation(value="退出",notes = "用户退出")
    public Response quit(HttpServletRequest request,String uuid){
       // UserInfo userInfo = (UserInfo)request.getSession().getAttribute(uuid);
        request.getSession().removeAttribute(uuid);
        return Response.ok();
    }
}
