package com.fiberhome.fp.service.impl;

import com.fiberhome.fp.dao.LogAnalzeDao;
import com.fiberhome.fp.listener.event.AnalyseProcess;
import com.fiberhome.fp.listener.event.FileStatus;
import com.fiberhome.fp.pojo.ErrorResult;
import com.fiberhome.fp.pojo.FpOperationTable;
import com.fiberhome.fp.pojo.LogAnalze;
import com.fiberhome.fp.service.FpProjectService;
import com.fiberhome.fp.service.LogAnalyzeService;
import com.fiberhome.fp.util.FileUtil;
import com.fiberhome.fp.util.Page;
import com.fiberhome.fp.util.Response;
import com.fiberhome.fp.util.ShellUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class LogAnalyzeServiceImpl implements LogAnalyzeService {


    static ConcurrentHashMap<String, Map<String, Boolean>> hashMap = new ConcurrentHashMap<>();

    Logger logging = LoggerFactory.getLogger(LogAnalyzeServiceImpl.class);
    @Autowired
    private LogAnalzeDao logAnalzeDao;
    @Autowired
    private FpProjectService fpProjectService;

    private ThreadPoolExecutor pool = FpProjectServiceImpl.pool;

    @Value("${upload.log.path}")
    private String uploadLogPath;
    @Autowired
    private AnalyseProcess analyseProcess;

    @Value("${update.conf.name}")
    String config;

    @Value("${update.conf.path}")
    String path;

    @Value("${shell.execute.path}")
    String shellPath;


    public Map<String, AnalyseProcess> map = AnalyseProcess.getMap();

    @Override
    public Response getLogListByParam(Page page, LogAnalze LogAnalze) {
        return null;
    }


    @Override
    public boolean createLogAnalze(LogAnalze logAnalze) {
        //  startAnalyse(logAnalze.getProjectName(), logAnalze.getAddress(), uuid);
        return logAnalzeDao.createLogAnalze(logAnalze);
    }

    @Override
    public List<LogAnalze> findLogAnalyseList(LogAnalze param, Page page) {
        List<LogAnalze> logAnalyseList = logAnalzeDao.findLogAnalyseList(param, page);
        return logAnalyseList;
    }


    @Override
    public boolean startAnalyse(String project, String location, String uuid, Long analyseTime) {
        String dir = uploadLogPath + File.separator + location + File.separator + project + File.separator + analyseTime;
        FileUtil.replacerConf(uploadLogPath + File.separator + config,
                "cl_dir=" + dir + File.separator,
                "business=" + project,
                "relief=" + location,
                "analyseTime=" + analyseTime,
                config);
        File direct = new File(dir);
        ArrayList<File> fileArrayList1 = new ArrayList<>();
        long byteSize = FileUtil.getByteSize(10);
        FileUtil.findAllSizeMore(direct, byteSize);
        FileUtil.getSizeLLesser(direct, byteSize, fileArrayList1);
        AnalyseProcess.init(uuid, fileArrayList1, project, location, analyseTime, dir);
        upload(map.get(uuid));
        return true;
    }


    public void upload(AnalyseProcess analyseProcess) {
        Map<String, FileStatus> fileMap = analyseProcess.getFileMap();
        String uuid = analyseProcess.getUuid();
        List<File> fileArrayList1 = map2FileList(fileMap);
        for (int i = 0; i < fileArrayList1.size(); i++) {
            File file = fileArrayList1.get(i);
            String filePath = file.getPath();
            pool.execute(() -> {
                //调用脚本方法
                String bashCommand = "sh /home/nebula/analys.sh " + filePath + "";
                //boolean upload = ShellUtil.execSh(bashCommand,uuid,filePath);
                boolean upload = ShellUtil.newShSuccess(bashCommand, uuid, filePath);
                AnalyseProcess analyseProcess1 = map.get(uuid);
                FileStatus fileStatus = analyseProcess1.getFileMap().get(filePath);
                LogAnalze logAnalze = fileStatus.setFinish(true);
                if (logAnalze != null) {
                    //如果本次分析完整
                    String objectSerializePath = analyseProcess1.getObjectSerializePath(uploadLogPath);
                    if (analyseProcess1.getFileMap().size() == 0) {
                        File file1 = new File(objectSerializePath);
                        if (file1.exists()) {
                            file1.delete();
                        }
                    } else {
                        FileUtil.ObjectInputStreamDisk(objectSerializePath);
                    }
                    logAnalzeDao.updateLogAnalze(logAnalze);
                    map.remove(uuid);
                }
            });
        }
    }


    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<ErrorResult> listErrorResult(Page page, ErrorResult errorResult) {
        if (StringUtils.isNotEmpty(errorResult.getPjName())) {
            String[] pjNames = errorResult.getPjName().split(",");
            errorResult.setPjNameList(new ArrayList<>(Arrays.asList(pjNames)));
        }
        if (StringUtils.isNotEmpty(errorResult.getPjLocation())) {
            String[] pjLocations = errorResult.getPjLocation().split(",");
            errorResult.setPjLocationList(new ArrayList<>(Arrays.asList(pjLocations)));
        }
        if (StringUtils.isNotEmpty(errorResult.getTag())) {
            String[] tagList = errorResult.getTag().split(",");
            errorResult.setTagList(new ArrayList<>(Arrays.asList(tagList)));
        }
        return logAnalzeDao.ListErrResult(page, errorResult);
    }

    @Override
    public List<FpOperationTable> listOperation(Page page, FpOperationTable fpOperationTable) {
        if (StringUtils.isNotEmpty(fpOperationTable.getPjName())) {
            String[] pjNames = fpOperationTable.getPjName().split(",");
            fpOperationTable.setPjNameList(new ArrayList<>(Arrays.asList(pjNames)));
        }
        if (StringUtils.isNotEmpty(fpOperationTable.getPjLocation())) {
            String[] pjLocations = fpOperationTable.getPjLocation().split(",");
            fpOperationTable.setPjLocationList(new ArrayList<>(Arrays.asList(pjLocations)));
        }

        if (StringUtils.isNotEmpty(fpOperationTable.getLogLeave())) {
            String[] logLeaveList = fpOperationTable.getLogLeave().split(",");
            fpOperationTable.setLogLeaveList(new ArrayList<>(Arrays.asList(logLeaveList)));
        }
        return logAnalzeDao.list(page, fpOperationTable);
    }

    @Override
    public Response batchDeleteLogAnaylse(List<String> uuids) {
        for (String uuid : uuids) {
            if (map.containsKey(uuid)) {
                map.remove(uuid);
            }
            logAnalzeDao.deleteLogAnalze(uuid);
        }
        return null;
    }


    private List<File> map2FileList(Map<String, FileStatus> fileMap) {
        ArrayList<File> list = new ArrayList<>();
        for (Map.Entry<String, FileStatus> entry : fileMap.entrySet()) {
            File file = new File(entry.getValue().getFilePath());
            if (file.exists()) {
                list.add(file);
            }
        }
        return list;
    }

}
