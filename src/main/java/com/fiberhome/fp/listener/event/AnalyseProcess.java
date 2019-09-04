package com.fiberhome.fp.listener.event;


import com.fiberhome.fp.pojo.LogAnalze;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AnalyseProcess {
    /*  public ConcurrentHashMap<String, List<Boolean>> hashMap = new ConcurrentHashMap();*/

    public static ConcurrentHashMap<String, AnalyseProcess> map = new ConcurrentHashMap<>();
    //进程
    private int process;
    private boolean isFinish;
    private boolean isShow;
    private boolean isAnalyseError;
    private int count;
    private int finishCount;
    private int showCount;
    private int successCount;
    private Map<String, FileStatus> fileMap;
    private Map<String, String> errorResultMap;

    private String projectName;
    private String projectLocation;
    private Long createTime;
    private String uuid;


    public static ConcurrentHashMap<String, AnalyseProcess> getMap() {
        return map;
    }

    public static void setMap(ConcurrentHashMap<String, AnalyseProcess> map) {
        AnalyseProcess.map = map;
    }

    public static void init(String uuid, List<File> fileList, String projectName, String projectLocation, Long createTime, String rootPath) {

        HashMap<String, FileStatus> statusHashMap = new HashMap<>();
        AnalyseProcess analyseProcess = new AnalyseProcess();
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
        }
        analyseProcess.setCount(fileList.size());
        analyseProcess.setFileMap(statusHashMap);
        analyseProcess.setProjectName(projectName);
        analyseProcess.setProjectLocation(projectLocation);
        analyseProcess.setCreateTime(createTime);
        analyseProcess.setErrorResultMap(new HashMap<String, String>());
        analyseProcess.setUuid(uuid);
        map.put(uuid, analyseProcess);
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
        this.process = process;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public Map<String, FileStatus> getFileMap() {
        return fileMap;
    }

    public void setFileMap(Map<String, FileStatus> fileMap) {
        this.fileMap = fileMap;
    }


    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {
        synchronized (this.getClass()) {
            this.showCount = showCount;
            if ((showCount * 100) / count == 100) {
                setShow(true);
            }
        }
    }

    public Map<String, String> getErrorResultMap() {
        return errorResultMap;
    }

    public void setErrorResultMap(Map<String, String> errorResultMap) {
        this.errorResultMap = errorResultMap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFinishCount() {
        return finishCount;
    }

    public LogAnalze setFinishCount(int finishCount) {
        synchronized (this.getClass()) {
            this.finishCount = finishCount;
            if ((finishCount * 100 / count) == 100) {
                LogAnalze logAnalze = adapt(this);
                return logAnalze;
            }
            return null;
        }

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
        synchronized (this.getClass()) {
            this.successCount = successCount;
            this.process = (successCount * 100) / count;
            if (process == 100) {
                this.isFinish = true;
            }
        }
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
        String path = rootPath+File.separator+uuid+"_Serialize)";
        return path;
    }
}
