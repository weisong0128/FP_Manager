package com.fiberhome.fp.service;

import com.fiberhome.fp.pojo.ErrorResult;
import com.fiberhome.fp.pojo.FpOperationTable;
import com.fiberhome.fp.util.Page;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
public interface ErrorResultService {


    /**
     * 不合格SQL集合
     * @param page
     * @param errorResult
     * @return
     */
    List<ErrorResult> listErrorResult(Page page,ErrorResult errorResult);


    /**
     * 报错信息集合
     * @param page
     * @param fpOperationTable
     * @return
     */
    List<FpOperationTable> listOperation(Page page, FpOperationTable fpOperationTable);
}
