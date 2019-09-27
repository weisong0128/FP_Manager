package com.fiberhome.fp.dao;

import com.fiberhome.fp.pojo.AllResult;
import com.fiberhome.fp.pojo.ErrorResult;
import com.fiberhome.fp.pojo.FpOperationTable;
import com.fiberhome.fp.pojo.LogAnalze;
import com.fiberhome.fp.util.Page;

import java.util.List;
import java.util.Map;

/**
 * 日志分析
 */
public interface LogAnalzeDao {
    /**
     * 创建日志
     *
     * @param logAnalze
     * @return
     */
    boolean createLogAnalze(LogAnalze logAnalze);

    /**
     * 修改
     *
     * @param
     * @return
     */
    public int updateLogAnalze(Map<String, String> param, String uuid);


    /**
     * 删除
     *
     * @param uuid
     * @return
     */
    int deleteLogAnalze(String uuid);

    LogAnalze findOneLogAnalyse(String uuid);

    List<ErrorResult> wordExportErrorResult(String pjName, String pjLocation, String createTime);

    List<FpOperationTable> wordExportFpOperationTable(String pjName, String pjLocation, String createTime);

    public List<LogAnalze> findLogAnalyseList(LogAnalze param, Page page);

    List<ErrorResult> listErrResult(Page page, ErrorResult errorResult);


    public int updateLogAnalze(LogAnalze logAnalze);

    public AllResult getProportion(String pjName, String pjLocation, String createTime);
}
