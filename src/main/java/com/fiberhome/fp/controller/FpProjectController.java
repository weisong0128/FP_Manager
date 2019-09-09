package com.fiberhome.fp.controller;

import com.fiberhome.fp.createTable.CheckExcel;
import com.fiberhome.fp.createTable.Table;
import com.fiberhome.fp.pojo.*;
import com.fiberhome.fp.service.AllResultService;
import com.fiberhome.fp.service.ErrorResultService;
import com.fiberhome.fp.service.FpHelpService;
import com.fiberhome.fp.service.FpProjectService;
import com.fiberhome.fp.util.*;
import com.fiberhome.fp.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jxl.read.biff.BiffException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
@Api(value = "FP功能接口" ,description = "前一个版本所有接口")
@RestController
@RequestMapping("/project/")
@PropertySource(value = "classpath:config/application.properties" ,encoding = "UTF-8")
public class FpProjectController {

    Logger logging = LoggerFactory.getLogger(FpProjectController.class);

    @Autowired
    FpProjectService fpProjectService;


    @Autowired
    AllResultService allResultService;

    @Autowired
    ErrorResultService errorResultService;

    @Autowired
    FpHelpService fpHelpService;



    /**
     * 获取所有项目及对应地市接口
     * @return
     */
    @ApiOperation(value="业务分析模块",notes = "获取所有项目及对应地市接口")
    @GetMapping(value = "all")
    public Response listProject(String pjName,Page page){
        return Response.ok(fpProjectService.listProject(pjName,page),page);
    }


    /**
     * 上传日志接口
     * @param fpProject
     * @return
     */
    @ApiOperation(value = "上传日志接口",notes = "上传日志接口")
    @ApiImplicitParam(name = "fpProject",value = "fpProject配置信息",required = false,dataType = "FpProject")
    @PostMapping(value = "upload")
    public Response uploadProject(FpProject fpProject){
        try {
            logging.info(String.format("日志上传内容路径：%s项目名：%s地市：%s",fpProject.getPath(),fpProject.getPjName(),fpProject.getPjLocation()));
            boolean ret = fpProjectService.upload(fpProject.getPath(),fpProject.getPjName(),fpProject.getPjLocation());
            if (ret){
                return Response.ok();
            }else {
                return Response.error("日志分析文件sfp_err_analysis.sh执行失败，请检查输入配置项");
            }
        }catch (Exception e){
            return Response.error("分析日志失败！");
        }
    }


    /**
     * sql展示详情接口
     * @param page
     * @param allResult
     * @return
     */
    @ApiOperation(value="所有sql详情",notes = "sql展示详情接口")
    @GetMapping(value = "list")
    public Response allResultList(Page page, AllResult allResult){
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("page",page);
        retMap.put("allResult",allResultService.getAllResult(page,allResult));
        return Response.ok(retMap);
    }

    //#####################AllResultController  start############


    /**
     * 统计图数据接口
     * @return
     */
    @ApiOperation(value="统计图数据接口",notes = "统计图数据接口")
    @GetMapping(value = "proportion")
    public Response proportionDate(AllResult allResult){
        Map<String, Object> retMap = new HashMap<>();
        AllResult dbAllResult = allResultService.getProportionDate(allResult.getPjName(),allResult.getPjLocation(),allResult.getSearchTime(),allResult.getStartTime(),allResult.getEndTime());
        retMap.put("qualifiedSql",dbAllResult.getQualifiedSql());
        retMap.put("unqualifiedSql",dbAllResult.getUnqualifiedSql());
        List<TagProporation> tagProporationList = dbAllResult.getTagProporationsList();
        List<SqlCount> sqlIssueCountList = dbAllResult.getSqlCountList();
        List<ErrorSqlCount> errorSqlCount = dbAllResult.getErrorSqlCountList();
//        List<String> tagList = new ArrayList<>();
//        List<Double> tagCountList = new ArrayList<>();
        List<String> hourList = new ArrayList<>();
        List<Double> sqlCountList = new ArrayList<>();
        List<String> yearMonthList = new ArrayList<>();
        List<Double> errorSqlCountList = new ArrayList<>();
//        if (tagProporationList != null && tagProporationList.size()>0){
//            for (TagProporation tp:tagProporationList){
//                tagList.add(tp.getTag());
//                tagCountList.add(tp.getCount());
//            }
//
//        }
        if (sqlIssueCountList != null && sqlIssueCountList.size()>0){
            for (SqlCount sc:sqlIssueCountList){
                hourList.add(sc.getHour());
                sqlCountList.add(sc.getCount());
            }

        }
        if (errorSqlCount != null && errorSqlCount.size()>0){
            for (ErrorSqlCount esc:errorSqlCount){
                yearMonthList.add(esc.getMonth());
                errorSqlCountList.add(esc.getCount());
            }
        }
        retMap.put("tag",tagProporationList);
//        retMap.put("tagCount",tagCountList);
        retMap.put("hour",hourList);
        retMap.put("sqlCount",sqlCountList);
        retMap.put("yearMonth",yearMonthList);
        retMap.put("errorCount",errorSqlCountList);

        return Response.ok(retMap);
    }

    /**
     * 不同类别字段展示详情接口
     * @param page
     * @param rowResult
     * @return
     */
    @ApiOperation(value="不同类别字段展示详情接口",notes = "不同类别字段展示详情接口")
    @GetMapping(value = "rowresult")
    public Response rowResultList(Page page, RowResult rowResult){
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("page",page);
        retMap.put("rowResult",allResultService.rowResultList(page,rowResult));
        return Response.ok(retMap);
    }

    /**
     * 获取所有sql类别接口
     * @return
     */
    @ApiOperation(value="获取所有sql类别接口（暂时不用）",notes = "获取所有sql类别接口")
    @GetMapping(value = "tags")
    public Response tagList(){
        Map<String, Object> retMap = new HashMap<>();
        return Response.ok(allResultService.tagList());
    }

    //#####################AllResultController  end############



    //#####################ErrorResultController  start############

    /**
     * 不合格sql展示界面接口
     * @param page
     * @param errorResult
     * @return
     */
    @ApiOperation(value="不合格sql详情",notes = "不合格sql展示界面接口")
    @GetMapping("result")
    public Response listErrorSql(Page page, ErrorResult errorResult){
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("page",page);
        retMap.put("errResult",errorResultService.listErrorResult(page,errorResult));
        return Response.ok(retMap);
    }

    /**
     * 报错信息界面展示接口
     * @param page
     * @param fpOperationTable
     * @return
     */

    @GetMapping("operation")
    @ApiOperation(value="所有错误信息详情页",notes = "报错信息界面展示接口")
    public Response listErrorSql(Page page, FpOperationTable fpOperationTable){
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("page",page);
        retMap.put("operation",errorResultService.listOperation(page,fpOperationTable));
        return Response.ok(retMap);
    }

    //#####################ErrorResultController  end############



    //#####################FpHelpController  start############

    /**
     * fp小叮当接口
     * @return
     */
    @ApiOperation(value="小叮当帮助页",notes = "fp小叮当接口")
    @GetMapping(value = "solution")
    public Response listFpHelp(Page page, String errKeyWord){
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("page",page);
        retMap.put("fpHelp",fpHelpService.getHelp(page,errKeyWord));
        return Response.ok(retMap);
    }

    //#####################FpHelpController  end############


    //#####################CreateTableController  start  #########

    @Value("${field.type.path}")
    String fieldPath;

    @Value("${field.type.file}")
    String fieldFileName;

    @Value("${upload.table.path}")
    String uploadTablePath;

    @Value("${newTable.template.fileName}")
    String  fileName;
    @Value("${newTable.template.filePath}")
    String filePath;



    /**
     * 上传excel接口
     * @return
     */
    @ApiOperation(value="建表模块-》上传excel接口",notes = "上传excel接口")
    @PostMapping(value = "/uploadexcel")
    public Response uploadExcel(MultipartFile file){
        boolean isExcel = false;
        try {
            isExcel = ExcelUtil.isExcel(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            logging.error("上传失败",e);
            return Response.error("文件上传失败！");
        }
        if (!isExcel){
            return Response.error("文件不是Excel格式！");
        }
        String fileName = file.getOriginalFilename();

        if (fileName.split("_").length != 4){
            return Response.error("文件命名格式应该为：地市_项目名称_业务调查表_日期.xls！");
        }

        String currentdate = String.valueOf(System.currentTimeMillis());
//        String path  = uploadTablePath + File.separator  + "uploadCreateTable" + currentdate;
        String path  = uploadTablePath + File.separator  + "uploadCreateTable" + currentdate;
        File director = new File(path);
        if (!director.exists()){
            director.mkdir();
        }
        try {
            file.transferTo(new File(director.getPath() + File.separator + fileName));
            logging.info(String.format("文件上传至：%s",path));
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error("文件上传失败！");
        }
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("fileName",fileName);
        retMap.put("path",path);

        return Response.ok(retMap);
    }


    /**
     * 下载excel模板接口
     * @return
     */
    @ApiOperation(value="建表模块-》下载excel模板接口",notes = "下载excel模板接口")
    @GetMapping(value = "/downloadexcel")
    public void  downloadexcel(HttpServletResponse response) {
        FileUtil.downloadFile(response,fileName,filePath);
    }


    /**
     * 校验excel接口
     * @return
     */
    @ApiOperation(value="建表模块-》校验excel",notes = "校验excel接口")
    @GetMapping(value = "/check")
    public Response check(String fileName,String path){
        File file1 = new File(path + File.separator  + fileName);
        File file2 = new File(  fieldPath + File.separator + fieldFileName);
//        File file2 = new File( uploadTablePath + File.separator + fieldPath + File.separator + fieldFileName);
        //CheckExcelModel checkExcelModel;
        List<CheckExcelModel> checkExcelModels;
        try {
            checkExcelModels = CheckExcel.CheckExecl(file1, file2);
            if (checkExcelModels.size()>0){
                checkExcelModels.get(0).setPath(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error("文件校验失败！");
        }
       /* if (!checkExcelModel.isSuccesss()){
            return Response.error(checkExcelModel);
        }*/
        return Response.ok(checkExcelModels);
    }

    /**
     * 修改excel接口
     * @return
     */
    @ApiOperation(value="建表模块-》修改excel接口",notes = "修改excel接口")
    @PostMapping(value = "/updateexcel")
    public Response updateExcel(@RequestBody List<UpdateExcelModel> updateExcelModel) {
        for (int i = 0; i <updateExcelModel.size() ; i++) {
            if (StringUtils.pathEquals(updateExcelModel.get(i).getUpdateType(),"T")){
                String fieldFile = fieldPath +  File.separator + fieldFileName;
//            String[] fields = updateExcelModel.getUpdateType().split(",");
                ExcelUtil.writeSpecifiedCell(fieldFile,0,updateExcelModel.get(i).getRow(),updateExcelModel.get(i).getCol(),updateExcelModel.get(i).getOldType());
                ExcelUtil.writeSpecifiedCell(fieldFile,0,updateExcelModel.get(i).getRow(),updateExcelModel.get(i).getCol()+1,updateExcelModel.get(i).getContent());
            }else {
                String excelFile = updateExcelModel.get(0).getPath() +  File.separator + updateExcelModel.get(i).getFileName();
                ExcelUtil.writeSpecifiedCell(excelFile, updateExcelModel.get(i).getSheetNum(), updateExcelModel.get(i).getRow(),updateExcelModel.get(i).getCol(), updateExcelModel.get(i).getContent());
            }
        }
        return Response.ok();
    }


    /**
     * 生成建表脚本
     * @return
     */
    @ApiOperation(value="建表模块-》生成建表脚本",notes = "生成建表脚本")
    @GetMapping(value = "/create")
    public Response create(String fileName,String path){
        File file1 = new File(path + File.separator  + fileName);
//        File file2 = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath() + File.separator + fieldPath + File.separator + fieldFileName);
        File file2 = new File( fieldPath + File.separator + fieldFileName);
        Table table = new Table();
        Map<String,String> map = new HashMap<>();
        try {
            map = table.PublicMuilt(file1,file2,path);
        } catch (BiffException e) {
            e.printStackTrace();
            return Response.error("生成脚本失败");
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error("生成脚本失败");
        }
        map.put("path",path);
        return Response.ok(map);
    }


    /**
     * 上传脚本 @RequestParam(value = "file",required = true)
     */
    @ApiOperation(value="建表模块-》上传脚本",notes = "上传脚本")
    @RequestMapping(value = "uploadSql",method = RequestMethod.POST,headers = "content-type=multipart/form-data")
    public Response uploadSql(@RequestParam(value = "file",required = true) MultipartFile file){
       return Response.ok( FileUtil.uploadSql(file,uploadTablePath));
    }

    /**
     * 下载接口
     * @return
     */
    @ApiOperation(value="建表模块-》下载脚本文件",notes = "下载接口")
    @GetMapping(value = "/download")
    public ResponseEntity<byte[]> downLoad(String sqlName, String path){
        //下载文件路径
        File file = new File(path + File.separator + sqlName);
        try{
            if(file.exists()){
                HttpHeaders headers = new HttpHeaders();
                //下载显示的文件名，解决中文名称乱码问题
                String downloadFielName = new String(sqlName.getBytes("UTF-8"), "iso-8859-1");
                //通知浏览器以attachment（下载方式）打开图片
                headers.setContentDispositionFormData("attachment", downloadFielName);
                //application/octet-stream ： 二进制流数据（最常见的文件下载）。
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
            }else{
                return null;
            }
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 元数据，数据入库
     * @param sqlName
     * @param path
     * @return
     */
    @ApiOperation(" 建表模块-》元数据，数据入库")
    @GetMapping(value = "metadate")
    public Response imputMetaDate(String sqlName,String path){

        String partition = TimeUtil.beforeFewMonth(0);

        File dictionary = new File(path);
        File[] files = dictionary.listFiles();
        StringBuilder retMsg = new StringBuilder();
        for (File file : files){
            String fileName = file.getName();
            if (!sqlName.equals(fileName) && fileName.contains(".sql")){
                try {
                    logging.info(String.format("执行建表入库命令：sh /opt/software/lsql/bin/load.sh -t CreateTable -p %s -tp sql -sp '\\t' -local -f %s" +
                            " -fl ProjectName,LocalName,TableName,UpdateTime,CreateScript",partition,path + File.separator + fileName));
                    boolean ret = ShellUtil.shSuccess("sh /opt/software/lsql/bin/load.sh -t CreateTable -p "+partition+" -tp sql -sp '\\t' -local -f "+path + File.separator + fileName +
                            " -fl ProjectName,LocalName,TableName,UpdateTime,CreateScript ");
                    if (ret){
                        logging.info(String.format("%s表建表入库成功",fileName));
                        retMsg.append(fileName+"表建表入库成功;");
                    }
                } catch (IOException e) {
                    logging.error(String.format("%s表建表入库失败",fileName),e);
                    retMsg.append(fileName+"表建表入库失败;");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    logging.error(String.format("%s表建表入库失败",fileName),e);
                    retMsg.append(fileName+"表建表入库失败;");
                    e.printStackTrace();
                }

            }
            if (fileName.contains(".txt")){
                try {
                    logging.info(String.format("执行元数据入库命令：sh /opt/software/lsql/bin/load.sh -t BusinessMet -p %s -tp txt -sp '\\t' -local -f  %s -fl " +
                            "ProjectName,LocalName,TableName,TableType,PhysicalName,FieldName,OldFieldType,FieldDes," +
                            "Exhibition,IndexT,OrderT,CountT,GroupT,LikeT,Multivalued,SuperLong,FulText,UpdateTime ",partition,path + File.separator + fileName ));
                    boolean ret = ShellUtil.shSuccess("sh /opt/software/lsql/bin/load.sh -t BusinessMet -p "+partition+" -tp txt -sp '\\t' -local -f " + path + File.separator + fileName +
                            " -fl ProjectName,LocalName,TableName,TableType,PhysicalName,FieldName,OldFieldType,FieldDes," +
                            "Exhibition,IndexT,OrderT,CountT,GroupT,LikeT,Multivalued,SuperLong,FulText,UpdateTime  ");
                    if (ret){
                        logging.info(String.format("%s表元数据入库成功",fileName));
                        retMsg.append(fileName+"表元数据入库成功;");
                    }
                } catch (IOException e) {
                    logging.error(String.format("%s表元数据入库失败",fileName),e);
                    retMsg.append(fileName+"表元数据入库失败;");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    logging.error(String.format("%s表元数据入库失败",fileName),e);
                    retMsg.append(fileName+"表元数据入库失败;");
                    e.printStackTrace();
                }
            }
        }
        return Response.ok(retMsg.toString());
    }


    //###############################CreateTableController   end##############################################


}
