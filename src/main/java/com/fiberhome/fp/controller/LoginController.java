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
import java.util.LinkedHashMap;
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
        if (userInfo1!=null){
            Map map = new HashMap();
            HttpSession session = request.getSession();
            session.setAttribute(userInfo1.getUuid(),userInfo1);
            map.put("uuid",userInfo1.getUuid());
            map.put("userRole",userInfo1.getUserRole());
            map.put("userName",userInfo1.getUserName());
            return Response.ok(map);
        }
        return Response.error("用户不存在或用户，密码错误！");
    }

    @GetMapping("quit")
    @ApiOperation(value="退出",notes = "用户退出")
    public Response quit(HttpServletRequest request,String uuid){
        //UserInfo userInfo = (UserInfo)request.getSession().getAttribute(uuid);
        request.getSession().removeAttribute(uuid);
        return Response.ok();
    }


    /**
     * 获取全部的用户信息
     */
  /*  public  boolean getUserInfo(HttpServletRequest request,UserInfo userInfo){
        boolean flag=false;
        Map<String,HttpSession> onlineUserList=(Map<String,HttpSession>)request.getSession().getServletContext().getAttribute("ONLINE_USERS");
        if(onlineUserList==null){
            onlineUserList=new LinkedHashMap<String,HttpSession>();
        }
        //如果当前用户存在其他session信息。那么就让旧的session失效
       *//* HttpSession oldSession=onlineUserList.get(userInfo.getUuid());
        if(oldSession!=null){
            oldSession.invalidate();
        }*//*
        if (!onlineUserList.containsKey(userInfo.getUuid())){
            //记录新的session,并记录到所有用户下
            onlineUserList.put(userInfo.getUuid(), request.getSession());
            request.getSession().getServletContext().setAttribute("ONLINE_USERS",onlineUserList);
        }else {
            flag=true;
        }
        return flag;
    }*/
}
