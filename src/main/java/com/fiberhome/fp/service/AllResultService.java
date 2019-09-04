package com.fiberhome.fp.service;

import com.fiberhome.fp.pojo.AllResult;
import com.fiberhome.fp.pojo.RowResult;
import com.fiberhome.fp.util.Page;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/2
 */

public interface AllResultService {


    /**
     * 业务分析sql展示详情
     * @param page
     * @param allResult
     * @return
     */
    List<AllResult> getAllResult(Page page, AllResult allResult);


    /**
     * 业务分析数据占比
     * @param pjName
     * @param pJLocation
     * @return
     */
    AllResult getProportionDate(String pjName,String pJLocation,String time,String startTime,String endTime);


    /**
     * 获取不同类别字段
     * @param page
     * @param rowResult
     * @return
     */
    List<RowResult> rowResultList(Page page, RowResult rowResult);

    /**
     * 获取所有sql类型
     * @return
     */
    List<AllResult> tagList();
}
