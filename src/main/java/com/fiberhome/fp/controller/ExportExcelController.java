package com.fiberhome.fp.controller;

import com.fiberhome.fp.pojo.AuthManage;
import com.fiberhome.fp.service.AuthManageService;
import com.fiberhome.fp.util.ExcelUtil;
import com.fiberhome.fp.util.Page;
import io.swagger.annotations.Api;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

//导出Excel
@Api(value = "授权管理导出Excel",description = "授权管理导出Excel")
@RestController
@RequestMapping("/export/")
public class ExportExcelController {

    @Resource
    AuthManageService authManageService;
    /**
     * 导出报表
     * @return
     */
    @GetMapping(value = "/export")
    @ResponseBody
    public void export(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取数据
        List<AuthManage> allAuthManage = authManageService.getAllAuthManage(null, new AuthManage());

        //excel标题
        String[] title = {"主键","项目名称","环境负责人","电话","安装省份","安装地市","安装地址","MAC","主节点ip","证书下载日期","备注（线上生产环境，研发测试环境）","对应的sn文件","授权反馈情况","备注","创建时间","修改时间"};

        //excel文件名
        String fileName = "授权管理信息"+System.currentTimeMillis()+".xls";

        //sheet名
        String sheetName = "授权管理信息";

        String[][] content= new String[allAuthManage.size()][];
        for (int i = 0; i < allAuthManage.size(); i++) {
            int b=0;
            content[i] = new String[title.length];
            AuthManage authManage = allAuthManage.get(i);
            content[i][b] = authManage.getUuid();
            content[i][++b] = authManage.getProjectName();
            content[i][++b] = authManage.getEnvirHead();
            content[i][++b] = authManage.getPhone();
            content[i][++b] = authManage.getProvinces();
            content[i][++b] = authManage.getCities();
            content[i][++b] = authManage.getAddress();
            content[i][++b] = authManage.getMac();
            content[i][++b] = authManage.getMasterIp();
            content[i][++b] = authManage.getDownloadTime();
            String envirnote ="";
            if("0".equals(authManage.getEnvirNote())){envirnote="线上生成环境";}
            if("1".equals(authManage.getEnvirNote())){envirnote="研发测试环境";}
            if("3".equals(authManage.getEnvirNote())){envirnote="已停用";}
            content[i][++b] = envirnote;
            content[i][++b] = authManage.getSnFile();
            String feedback ="";
            if("0".equals(authManage.getFeedback())){feedback="已反馈";}
            if("1".equals(authManage.getFeedback())){feedback="未反馈";}
            content[i][++b] = feedback;
            content[i][++b] = authManage.getCreateTime();
            // content[i][14] = authManage.getUpdateTime();
        }

        //创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);

        //响应到客户端
        try {
            this.setResponseHeader(response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ new String(fileName.getBytes("GB2312"),"ISO-8859-1"));
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}











