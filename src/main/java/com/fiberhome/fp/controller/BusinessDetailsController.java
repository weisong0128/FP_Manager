package com.fiberhome.fp.controller;

import com.fiberhome.fp.dao.impl.AllResultDaoImpl;
import com.fiberhome.fp.pojo.BusinessDetails;
import com.fiberhome.fp.service.BusinessDetailsService;
import com.fiberhome.fp.util.EntityMapTransUtils;
import com.fiberhome.fp.util.Page;
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

@Api(value = "表详情",description = "业务分析详情")
@RestController
@RequestMapping("/business/")
public class BusinessDetailsController {

    @Resource
    BusinessDetailsService businessDetailsService;

    @ApiOperation(value="表名使用频率",notes = "表名使用频率,  参数说明：pjname(项目名称)，pjLocations（安装地市字符串，如:(江苏南京,广西来宾)）,time(查询时间区间，" +
            "取值范围（one（近一个月），three（3个月），half（半年），year（一年））)")
    @GetMapping("getBusinessDetails")
    public Response getBusinessDetails(String pjName,String pjLocations,String time,String tableName, Page page){
        Map<String,Object> parames = new HashMap<>();
        List<String> pjLocation=null;
       if(pjLocations !=null){
          pjLocation = EntityMapTransUtils.StringToList(pjLocations);
           parames.put("pjLocations",pjLocation);
       }
    /*    List<String> tablePartitions=null;
        if (time != null){
            tablePartitions = AllResultDaoImpl.partitions(time);
            parames.put("tablePartitions",tablePartitions);
        }*/
        if (tableName != null){
            parames.put("tableName",tableName);
        }
        parames.put("pjName",pjName);
        return Response.ok(businessDetailsService.getBusinessDetails(parames, page),page);
    }
}
