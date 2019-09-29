package com.fiberhome.fp.controller;

import com.fiberhome.fp.listener.event.AnalyseProcess;
import com.fiberhome.fp.pojo.ErrorResult;
import com.fiberhome.fp.pojo.LogAnalze;
import com.fiberhome.fp.service.LogAnalyzeService;
import com.fiberhome.fp.util.FileUtil;
import com.fiberhome.fp.util.Page;
import com.fiberhome.fp.util.Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/log/")
public class LogAnalzeController {

    static Logger logging = LoggerFactory.getLogger(LogAnalzeController.class);

    private Map<String, AnalyseProcess> map = AnalyseProcess.getMap();


    @Value("${upload.log.path}")
    private String uploadLogPath;

    @Value("${template.file.dir}")
    String outFilePath;
    @Autowired
    private LogAnalyzeService logAnalyzeService;

    public static final int FINISHNUM = 100;

    /**
     * 上传日志文件接口
     *
     * @return
     */
    @PostMapping(value = "/batchLogUpload")
    public Response batchLogUpload(@RequestParam("file") MultipartFile[] file, String projectName, String projectLocation, String createTime) {
        if (StringUtils.isEmpty(projectName) || StringUtils.isEmpty(projectLocation) || StringUtils.isEmpty(createTime)) {
            return Response.error("请检查传入参数是否为空!");
        }
        File director = new File(uploadLogPath);
        if (!director.exists()) {
            director.mkdirs();
        }
        for (MultipartFile multipartFile : file) {
            String fileName = multipartFile.getOriginalFilename();
            if (!fileName.toLowerCase().contains("cl.log")) {
                return Response.error("文件命名格式应该为：cl.log*  ！");
            }
            String pt = uploadLogPath + File.separator + projectLocation + File.separator + projectName + File.separator + createTime;
            File ptFile = new File(pt);
            if (!ptFile.exists()) {
                ptFile.mkdirs();
            }
            File file1 = new File(pt + File.separator + fileName);
            try {
                multipartFile.transferTo(file1);
                logging.info("日志文件上传至 {}", pt);
                return Response.ok();
            } catch (Exception e) {
                logging.error(e.getMessage(), e);
                logging.error(e.toString());
                return Response.error("上传失败！");
            }
        }
        return Response.error();
    }

    /**
     * 开始分析按钮
     */

    @GetMapping("/startAnalyse")
    public Response startAnalyse(String projectName, String projectLocation, String createTime, String userId) {
        if (StringUtils.isEmpty(projectName) || StringUtils.isEmpty(projectLocation) || StringUtils.isEmpty(createTime)) {
            return Response.error("请检查传入参数是否为空");
        }
        String uuid = UUID.randomUUID().toString();
        long timeLong = Long.parseLong(createTime);
        LogAnalze logAnalze = new LogAnalze(projectName, projectLocation, timeLong, userId);
        logAnalze.setUuid(uuid);

        try {
            logAnalyzeService.createLogAnalze(logAnalze);
            logAnalyzeService.startAnalyse(projectName, projectLocation, uuid, timeLong);
        } catch (Exception e) {
            logging.error(e.getMessage(), e);
            return Response.error("分析日志失败");
        }

        HashMap<String, String> returnMap = new HashMap<>();
        returnMap.put("uuid", uuid);
        return Response.ok(returnMap);
    }


    /**
     * 轮训查看进程
     */
    @PostMapping("/getAnalyseProcess")
    public Response getAnalyseProcess(@RequestBody String uuids) {
        uuids = uuids.replace("\"", "");
        String[] split = uuids.split(",");
        ArrayList<Object> arrayList = new ArrayList<>();
        try {
            for (String uuid : split) {
                AnalyseProcess analyseProcess = map.get(uuid);
                if (analyseProcess != null && analyseProcess.getFileMap() != null) {
                    Map<String, Object> returnMap = new LinkedHashMap<>();
                    int process = analyseProcess.getProcess();
                    boolean show = analyseProcess.isShow();
                    List<String> errorResultList = analyseProcess.getErrorResultList();
                    boolean analyseError = analyseProcess.isAnalyseError();
                    returnMap.put("uuid", uuid);
                    returnMap.put("process", process);
                    returnMap.put("show", show);
                    returnMap.put("isError", analyseError);
                    returnMap.put("finish", analyseProcess.isFinish());
                    List<String> list = deDuplicationList(errorResultList);
                    returnMap.put("errorResultList", list);
                    arrayList.add(returnMap);
                }
            }
        } catch (Exception e) {
            logging.error(e.getMessage(), e);
            Response.error();
        }
        return Response.ok(arrayList);
    }

    private List<String> deDuplicationList(List<String> list1) {
        ArrayList<String> list = new ArrayList<>();
        if (list1 != null && !list1.isEmpty()) {
            for (String s : list1) {
                if (!list.contains(s)) {
                    list.add(s);
                }
            }
        }
        return list;
    }

    /**
     * 根据条件返回分析list
     */
    @GetMapping("/getLogListByParam")
    public Response getLogListByParam(Page page, LogAnalze logAnalze) {
        for (Map.Entry<String, AnalyseProcess> entry : map.entrySet()) {
            String uuid = entry.getKey();
            AnalyseProcess analyseProcess = entry.getValue();
            if (analyseProcess.isFinish() || analyseProcess.getProcess() == FINISHNUM) {
                map.remove(uuid);
                continue;
            }
        }
        HashMap<String, Object> hashMap = null;
        try {
            List<LogAnalze> logAnalyseList = logAnalyzeService.findLogAnalyseList(logAnalze, page);
            hashMap = new HashMap<>();
            hashMap.put("page", page);
            hashMap.put("data", logAnalyseList);
        } catch (Exception e) {
            logging.error(e.getMessage(), e);
            Response.error();
        }
        return Response.ok(hashMap);
    }

    /**
     * 根据条件获取分析错误sql列表
     * param 项目名称,项目地点 , 分析时间 , 关键字 ,时间范围,是否去重, sql错误类型
     */
    @GetMapping("/getSqlErroListByParam")
    public Response getSqlErroListByParam(Page page, ErrorResult errorResult) {
        Map<String, Object> retMap = null;
        try {
            retMap = new HashMap<>();
            retMap.put("page", page);
            retMap.put("errResult", logAnalyzeService.listErrorResult(page, errorResult));
        } catch (Exception e) {
            logging.error(e.getMessage(), e);
            Response.error();
        }
        return Response.ok(retMap);
    }

    @PostMapping("/batchDeleteLogAnaylse")
    public Response batchDeleteLogAnaylse(@RequestBody String uuids) {
        uuids = uuids.replace("\"", "");
        String[] split = uuids.split(",");
        ArrayList<String> list = new ArrayList<>();
        for (String s : split) {
            list.add(s);
        }
        try {
            Boolean aBoolean = logAnalyzeService.batchDeleteLogAnaylse(list);
            if (!aBoolean) {
                return Response.error("删除失败");
            }
        } catch (Exception e) {
            logging.error(e.getMessage(), e);
            return Response.error("删除失败");
        }

        return Response.ok();
    }

    @GetMapping("/restartAnalyse")
    public Response restartAnalyse(String pjName, String pjLocation, String createTime, String uuid) {
        if (StringUtils.isEmpty(pjName) || StringUtils.isEmpty(pjLocation) || StringUtils.isEmpty(createTime) || StringUtils.isEmpty(uuid)) {
            return Response.error("请检查传入参数是否为空");
        }
        String pt = uploadLogPath + File.separator + pjLocation + File.separator + pjName + File.separator + createTime;
        AnalyseProcess analyseProcess = map.get(uuid);
        if (analyseProcess == null) {
            //从硬盘反序列化对象,重新放进map中
            String serializePath = pt + File.separator + uuid + "_Serialize";
            boolean fileExixts = FileUtil.isFileExixts(serializePath);
            if (fileExixts) {
                try {
                    analyseProcess = (AnalyseProcess) FileUtil.objectInputStreamDisk(serializePath);
                    analyseProcess.setFinishCount(analyseProcess.getFinishCount() - analyseProcess.getUnSuccessFileMap().size());
                    analyseProcess.setFinish(false);
                    analyseProcess.setErrorResultMap(new ConcurrentHashMap<>());
                    analyseProcess.setAnalyseError(false);
                    map.put(uuid, analyseProcess);
                    logAnalyzeService.upload(analyseProcess);
                } catch (Exception e) {
                    logging.error(e.getMessage(), e);
                    logging.error("重新分析失败");
                }
            } else {
                logging.info("序列化文件不存在");
                logAnalyzeService.startAnalyse(pjName, pjLocation, uuid, Long.valueOf(createTime));
                // return Response.error("序列化文件不存在,无法重新分析.请重新上传日志文件");
            }
        }
        return Response.ok(uuid);
    }

    @GetMapping("/wordExport")
    public void wordExport(String uuid, HttpServletResponse response) {
        LogAnalze logAnalze = logAnalyzeService.findOneLogAnalyse(uuid);
        String pjName = logAnalze.getProjectName();
        String pjLocation = logAnalze.getAddress();
        Long createTime = logAnalze.getCreateTime();
        String fileName = logAnalyzeService.wordExport(pjName, pjLocation, createTime + "");
        FileUtil.downloadFile(response, fileName, outFilePath);

    }
}
