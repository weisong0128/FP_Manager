package com.fiberhome.fp.dao;

import com.fiberhome.fp.pojo.AllResult;
import com.fiberhome.fp.pojo.RowResult;
import com.fiberhome.fp.util.Page;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/3
 */
public interface AllResultDao {

    /**
     * 业务分析sql展示详情列表
     * @param page
     * @param allResult
     * @return
     */
    List<AllResult> listAllResult(Page page, AllResult allResult);

    /**\
     * 业务分析sql占比表格数据
     * @param pjNames
     * @param pjLocations
     * @return
     */
    AllResult getProportion(List<String> pjNames,List<String> pjLocations,String time,String startTime,String endTime);

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
