package com.fiberhome.fp.listener.event;

import com.fiberhome.fp.pojo.LogAnalze;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class FileStatus implements Serializable {
    private static final long serialVersionUID = -7763702119598597148L;
    private ConcurrentMap<String, AnalyseProcess> map = AnalyseProcess.map;
    private boolean isFinish;
    private boolean isShow;
    private String errorResult;
    private String uuid;
    private String filePath;
    private String originalLogFilePath;
    private boolean isSuccess;
    private int process;

    public boolean isFinish() {
        return isFinish;
    }

    public LogAnalze setFinish(boolean finish) {
        synchronized (FileStatus.class) {
            isFinish = finish;
            int finishCount = getAnalyseProcess().getFinishCount();
            return getAnalyseProcess().setFinishCount(finishCount + 1);
        }
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        synchronized (FileStatus.class) {
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
        synchronized (FileStatus.class) {
            isSuccess = success;
            int successCount = getAnalyseProcess().getSuccessCount();
            getAnalyseProcess().setSuccessCount(successCount + 1);
        }
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }
}
