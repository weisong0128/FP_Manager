package com.fiberhome.fp.service;

import com.fiberhome.fp.listener.event.AnalyseProcess;
import com.fiberhome.fp.pojo.ErrorResult;
import com.fiberhome.fp.pojo.LogAnalze;
import com.fiberhome.fp.util.Page;
import com.fiberhome.fp.util.Response;

import java.util.List;

public interface LogAnalyzeService {

    Response getLogListByParam(Page page, LogAnalze logAnalze);

    boolean createLogAnalze(LogAnalze logAnalze);

    List<LogAnalze> findLogAnalyseList(LogAnalze param, Page page);

    public boolean startAnalyse(String project, String location, String uuid, Long analyseTime);

    public List<ErrorResult> listErrorResult(Page page, ErrorResult errorResult);

    public Boolean batchDeleteLogAnaylse(List<String> uuids);

    public void upload(AnalyseProcess analyseProcess);

    public LogAnalze findOneLogAnalyse(String uuid);

    public String wordExport(String pjName, String pjLocation, String createTime);

}
