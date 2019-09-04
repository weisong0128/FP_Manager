package com.fiberhome.fp.listener.event;

import com.fiberhome.fp.pojo.LogAnalze;
import com.fiberhome.fp.util.FileUtil;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileStatus {

    private ConcurrentHashMap<String, AnalyseProcess> map = AnalyseProcess.map;
    private boolean isFinish;
    private boolean isShow;
    private String errorResult;
    private String uuid;
    private String filePath;
    private String originalLogFilePath;
    private boolean isSuccess;

    public boolean isFinish() {
        return isFinish;
    }

    public LogAnalze setFinish(boolean finish) {
        synchronized (this.getClass()) {
            isFinish = finish;
            int finishCount = getAnalyseProcess().getFinishCount();
            LogAnalze logAnalze = getAnalyseProcess().setFinishCount(finishCount + 1);
            return logAnalze;
        }
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        synchronized (this.getClass()) {
            isShow = show;
            int showCount = getAnalyseProcess().getShowCount();
            getAnalyseProcess().setShowCount(showCount + 1);
        }
    }


    public String getErrorResult() {
        return errorResult;
    }

    public void setErrorResult(String errorResult) {
        this.errorResult = errorResult;
        Map<String, String> errorResultMap = getAnalyseProcess().getErrorResultMap();
        errorResultMap.put(filePath, errorResult);

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public AnalyseProcess getAnalyseProcess() {
        return map.get(uuid);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOriginalLogFilePath() {
        return originalLogFilePath;
    }

    public void setOriginalLogFilePath(String originalLogFilePath) {
        this.originalLogFilePath = originalLogFilePath;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        synchronized (this.getClass()) {


            isSuccess = success;
            int successCount = getAnalyseProcess().getSuccessCount();
            getAnalyseProcess().setSuccessCount(successCount + 1);
            getAnalyseProcess().getFileMap().remove(filePath);
            FileUtil.deleteDirect(filePath);
            if (originalLogFilePath != null && FileUtil.getDirectSize(new File(filePath).getParent()) == 0) {
                FileUtil.deleteDirect(originalLogFilePath);
            }
        }
    }
}
