package com.fiberhome.fp.listener.event;


import com.fiberhome.fp.pojo.LogAnalze;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 */
@Component
public class AnalyseProcess implements Serializable {
    static Logger logging = LoggerFactory.getLogger(AnalyseProcess.class);
    public static final ConcurrentMap<String, AnalyseProcess> map = new ConcurrentHashMap<>();
    //进程
    private int process;
    private boolean isFinish;
    private boolean isShow;
    private boolean isAnalyseError;
    private int count;
    private int finishCount;
    private int showCount;
    private int successCount;
    private ConcurrentMap<String, FileStatus> fileMap;
    private ConcurrentMap<String, FileStatus> unSuccessFileMap;
    private ConcurrentMap<String, String> errorResultMap;
    private String projectName;
    private String projectLocation;
    private Long createTime;
    private String uuid;
    private String uploadFileRootPath;

    private Long cutfilesize;


    public static ConcurrentMap<String, AnalyseProcess> getMap() {
        return map;
    }

    public static void fileSizeInit(String uuid, Long cutfilesize) {
        AnalyseProcess analyseProcess = new AnalyseProcess();
        analyseProcess.setCutfilesize(cutfilesize);
        map.put(uuid, analyseProcess);
    }

    public static void init(String uuid, List<File> fileList, String projectName, String projectLocation, Long createTime, String rootPath) {
        ConcurrentHashMap<String, FileStatus> statusHashMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, FileStatus> statusHashMap2 = new ConcurrentHashMap<>();
        AnalyseProcess analyseProcess = map.get(uuid);
        for (File file : fileList) {
            FileStatus fileStatus = new FileStatus();
            fileStatus.setUuid(uuid);
            fileStatus.setFilePath(file.getPath());
            String originalLogFileName = null;
            if (file.getParentFile().isDirectory() && file.getParentFile().getName().endsWith("cut")) {
                originalLogFileName = file.getParentFile().getName().replace(".cut", "");
                String path = rootPath + File.separator + originalLogFileName;
                if (new File(path).exists()) {
                    fileStatus.setOriginalLogFilePath(path);
                }
            }
            statusHashMap.put(file.getPath(), fileStatus);
            statusHashMap2.put(file.getPath(), fileStatus);
        }
        analyseProcess.setCount(fileList.size());
        analyseProcess.setFileMap(statusHashMap);
        analyseProcess.setProjectName(projectName);
        analyseProcess.setProjectLocation(projectLocation);
        analyseProcess.setCreateTime(createTime);
        analyseProcess.setErrorResultMap(new ConcurrentHashMap<>());
        analyseProcess.setUuid(uuid);
        analyseProcess.setUnSuccessFileMap(statusHashMap2);
        analyseProcess.setUploadFileRootPath(rootPath);
    }

    public List<String> getErrorResultList() {
        ArrayList<String> list = new ArrayList<>();
        if (errorResultMap != null) {
            Set<Map.Entry<String, String>> entries = errorResultMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                list.add(entry.getValue());
            }
        }
        return list;
    }

    public void setProcess(int process) {
        synchronized (AnalyseProcess.class) {
            this.process = process;
        }
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
        if (successCount != count) {
            setAnalyseError(true);
        }
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public ConcurrentMap<String, FileStatus> getFileMap() {
        return fileMap;
    }

    public void setFileMap(ConcurrentMap<String, FileStatus> fileMap) {
        this.fileMap = fileMap;
    }


    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
        this.process = ((successCount + this.showCount) * 50) / count;
        logging.info("项目名{},项目地市{} , 分析百分比{},是否成功{},是否可看{} ", projectName, projectLocation, process, isFinish, isShow);
        if (this.showCount == count) {
            setShow(true);
            logging.info("分析任务完成50% ,项目名{},项目地市{} , 是否可看{} ", projectName, projectLocation, isShow);
        }
    }

    public Long getCutfilesize() {
        return cutfilesize;
    }

    public void setCutfilesize(Long cutfilesize) {
        this.cutfilesize = cutfilesize;
    }

    public ConcurrentMap<String, String> getErrorResultMap() {
        return errorResultMap;
    }

    public String getUploadFileRootPath() {
        return uploadFileRootPath;
    }

    public void setUploadFileRootPath(String uploadFileRootPath) {
        this.uploadFileRootPath = uploadFileRootPath;
    }

    public void setErrorResultMap(ConcurrentMap<String, String> errorResultMap) {
        this.errorResultMap = errorResultMap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ConcurrentMap<String, FileStatus> getUnSuccessFileMap() {
        return unSuccessFileMap;
    }

    public void setUnSuccessFileMap(ConcurrentMap<String, FileStatus> unSuccessFileMap) {
        this.unSuccessFileMap = unSuccessFileMap;
    }

    public void setUnSuccessFileMap(String path, FileStatus fileStatus) {
        this.unSuccessFileMap.put(path, fileStatus);
    }

    public int getFinishCount() {
        return finishCount;
    }

    public LogAnalze setFinishCount(int finishCount) {
        this.finishCount = finishCount;
        if ((finishCount * 100 / count) == 100) {
            setFinish(true);
            LogAnalze logAnalze = adapt(this);
            logging.info("已全部分析完毕 ,更新数据{}", logAnalze);
            return logAnalze;
        }
        return null;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public int getProcess() {
        return process;
    }

    public boolean isAnalyseError() {
        return isAnalyseError;
    }

    public void setAnalyseError(boolean analyseError) {
        isAnalyseError = analyseError;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
        setProcess(((successCount + showCount) * 50) / count);
        if (process == 100) {
            this.isFinish = true;
        }
        logging.info("项目名{},项目地市{} , 分析百分比{},是否成功{},是否可看{} ", projectName, projectLocation, process, isFinish, isShow);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public static LogAnalze adapt(AnalyseProcess analyseProcess) {
        LogAnalze logAnalze = new LogAnalze();
        logAnalze.setUuid(analyseProcess.getUuid());
        logAnalze.setAddress(analyseProcess.getProjectLocation());
        logAnalze.setCreateTime(analyseProcess.getCreateTime());
        logAnalze.setProjectName(analyseProcess.getProjectName());
        logAnalze.setUpdateTime(new Date());
        StringBuilder builder = new StringBuilder();
        for (String s : analyseProcess.getErrorResultList()) {
            builder.append(s + ",");
        }
        logAnalze.setResult(builder.toString());
        logAnalze.setParsingState(analyseProcess.isAnalyseError() ? "1" : "0");
        logAnalze.setProgress(analyseProcess.getProcess() + "");
        return logAnalze;
    }

    public String getObjectSerializePath(String rootPath) {
        return rootPath + File.separator + uuid + "_Serialize";
    }

    @Override
    public String toString() {
        return "AnalyseProcess{" +
                "process=" + process +
                ", isFinish=" + isFinish +
                ", isShow=" + isShow +
                ", isAnalyseError=" + isAnalyseError +
                ", count=" + count +
                ", finishCount=" + finishCount +
                ", showCount=" + showCount +
                ", successCount=" + successCount +
                ", fileMap=" + fileMap +
                ", errorResultMap=" + errorResultMap +
                ", projectName='" + projectName + '\'' +
                ", projectLocation='" + projectLocation + '\'' +
                ", createTime=" + createTime +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
