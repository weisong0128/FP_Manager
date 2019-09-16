package com.fiberhome.fp.controller;

import com.fiberhome.fp.pojo.UserInfo;
import com.fiberhome.fp.service.UserInofService;
import com.fiberhome.fp.util.EntityMapTransUtils;
import com.fiberhome.fp.util.Page;
import com.fiberhome.fp.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "用户信息",description = "用户信息")
@RestController
@RequestMapping("/user/")
public class UserInfoController {
    @Autowired
    UserInofService userInfoService;
    /***
     * 创建用户
     * @param userInfo  用户实体
     */
    @ApiOperation(value="创建用户",notes = "创建用户信息")
    @PostMapping(value = "createUser")
    public Response createUser(@RequestBody UserInfo userInfo){
        return Response.ok(userInfoService.createUser(userInfo));
    };

    /**
     * 修改用户信息
     * @param userInfo
     * @return
     */
    @ApiOperation(value="修改用户信息",notes = "修改用户信息")
    @RequestMapping(value = "updateUserInfo",method = RequestMethod.POST)
    public Response updateUserInfo(@RequestBody UserInfo userInfo){
        return Response.ok(userInfoService.updateUserInfo(userInfo));
    };

    /**
     * 获取所有的用户信息
     */
    @ApiOperation(value="查询所有用户",notes = "查询所有用户信息")
    @GetMapping(value = "getAllUserInfo")
    public Response getAllUserInfo(Page page){
        return Response.ok(userInfoService.getAllUserInfo(page,null),page);
    };


    /**
     * 根据条件查询用户信息
     */
    @ApiOperation(value="根据条件查询用户信息(不加条件查询全部)",notes = "根据条件查询用户信息")
    @GetMapping(value = "getUserInfoByParames")
    public Response getUserInfoByParames(Page page,String keyWord,String userRole ,String userState ){
        UserInfo userInfo= new UserInfo();
        userInfo.setUserName(keyWord);
        userInfo.setUserRole(userRole);
        userInfo.setUserState(userState);
        return Response.ok(userInfoService.getAllUserInfo(page,userInfo),page);
    };


    /**
     * 获取所有的用户信息
     */
    @ApiOperation(value="查询单个用户",notes = "查询单个用户信息")
    @GetMapping(value = "getUserInfoByUuid")
    public Response getUserInfoByUuid(HttpServletRequest request,String uuid){
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute(uuid);
        return Response.ok(userInfo);
    }

    /**
     * 删除用户信息
     */
    @ApiOperation(value="删除,启用，停用 ",notes ="参数说明：" +
            "state:0:启用，1:停用 ,2:删除" +
            ",uuids:批量删除id字符串，如：cb712714d7374e1f9b7da0edada52ea4,cb712714d7374e1f9b7da0edada52ea4 ")
    @PostMapping(value = "updateState")
    public Response updateState(@RequestParam String state,@RequestParam String uuids){
        List<String> uuid = EntityMapTransUtils.StringToList(uuids);
        Map<String,Object> map = new HashMap<>();
        map.put("uuids",uuid);
        return Response.ok(userInfoService.updateState(state,map));
    }
}
