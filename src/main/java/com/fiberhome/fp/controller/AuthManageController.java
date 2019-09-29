package com.fiberhome.fp.controller;

        import com.fiberhome.fp.pojo.AuthManage;
        import com.fiberhome.fp.service.AuthManageService;
        import com.fiberhome.fp.util.EntityMapTransUtils;
        import com.fiberhome.fp.util.Page;
        import com.fiberhome.fp.util.Response;
        import io.swagger.annotations.Api;
        import io.swagger.annotations.ApiImplicitParam;
        import io.swagger.annotations.ApiOperation;
        import org.springframework.web.bind.annotation.*;

        import javax.annotation.Resource;
        import java.util.*;

@Api(value = "授权管理",description = "授权管理")
@RestController
@RequestMapping("/manage/")
public class AuthManageController {
    @Resource
    AuthManageService authManageService;

    @ApiOperation(value="创建授权管理",notes = "创建授权管理")
    @PostMapping("createAuthManage")
    public Response createAuthManage(@RequestBody AuthManage authManage){
        return Response.ok(authManageService.createAuthManage(authManage));
    }


    @ApiOperation(value="获取授权管理列表",notes = "获取授权管理列表")
    @GetMapping("getAllAuthManage")
    public Response getAllAuthManage(Page page,String projectName, String cities, String envirNote, String feedback, String keyWord, String sortField, String startTime, String endTime) {
        AuthManage authManage = new AuthManage(projectName,cities,envirNote,feedback,keyWord,sortField,startTime,endTime);
        return Response.ok(authManageService.getAllAuthManage(page,authManage),page);
    }


    @ApiOperation(value="修改授权管理",notes = "授权管理实体")
    @PostMapping("updateAuthManage")
    public Response updateAuthManage(@RequestBody AuthManage authManage) {
        return Response.ok(authManageService.updateAndDelete(EntityMapTransUtils.entityToMap1(authManage)));
    }

    @ApiOperation(value="删除授权管理",notes = "参数说明：传个字符串，如：'430105f0ba634519982caec19f3d29a4','bf49eaf70bd3408690dd62833979b68c'")
    @GetMapping("deleteAuthManage")
    public Response deleteAuthManage(@RequestParam String uuids) {
        List<String> uuid = EntityMapTransUtils.StringToList(uuids);
        Map<String,Object> map = new HashMap<>();
        map.put("uuids",uuid);
        return Response.ok(authManageService.updateAndDelete(map));
    }

    @ApiOperation(value="获取安装地市",notes = "")
    @GetMapping("getAllCities")
    public Response getAllCities() {
        return Response.ok(authManageService.getAllCities());
    }

    @ApiOperation(value="获取项目名称",notes = "")
    @GetMapping("getAllPjName")
    public Response getAllPjName() {
        return Response.ok(authManageService.getAllPjName());
    }

}
