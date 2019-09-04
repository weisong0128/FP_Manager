package com.fiberhome.fp.controller;

import com.fiberhome.fp.pojo.AuthManage;
import com.fiberhome.fp.service.IndexDataService;
import com.fiberhome.fp.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "首页数据",description = "首页数据展示")
@RestController
@RequestMapping("/index/")
public class IndexController {
    @Resource
    IndexDataService indexDataService;

    @ApiOperation(value="获取授权管理统计数据信息",notes = "uuid:全部的，envirNote1线上的，envirNote2测试的")
    @GetMapping("authManageCount")
    public Response authManageCount(){
       Map<String,Object> map = new HashMap<>();
        /*map.put("envirNote",envirNote);*/
        return Response.ok(indexDataService.authManageCount(map));
    }

    @ApiOperation(value="根据省份获取授权管理统计数据信息",notes = "参数说明：provinces -> 省份; 返回值说明：envirNote1：线上环境 envirNote2：测试环境")
    @GetMapping("authManageCountByProv")
    public  Response authManageCountByProv(String provinces){
        Map<String,Object> map = new HashMap<>();
        map.put("provinces",provinces);
        return Response.ok(indexDataService.authManageCountByProv(map));
    }


    @ApiOperation(value="各项目线上环境授权TOP10",notes = "返回值说明：项目名，线上，测试环境授权个数")
    @GetMapping("authManageTOP10")
    public Response authManageTOP10() {
        return  Response.ok(indexDataService.authManageTOP10(new HashMap<>()));
    }

    @ApiOperation(value="FP数据库地区分布TOP5",notes = "返回值说明：地市-授权个数（线上，测试个数）（参数：citiesCount 暂时不用）")
    @GetMapping("authManageTOP5")
    public Response authManageTOP5() {
        return Response.ok(indexDataService.authManageTOP5(new HashMap<>()));
    }

    @ApiOperation(value="开放授权趋势",notes = "返回值说明：月份-授权个数")
    @GetMapping("openAuthManage")
    public Response openAuthManage() {
        return Response.ok(indexDataService.openAuthManage(new HashMap<>()));
    }

    @ApiOperation(value="获取全国开放授权趋势",notes = "返回参数说明：onlinecount（线上环境个数），testcount（测试环境个数），provinces（省份（这个用不到））")
    @GetMapping("getAllauthManage")
    public Response getAllauthManage() {
        return Response.ok(indexDataService.getAllauthManage(new HashMap<>()));
    }

}
