package com.fiberhome.fp.dao;

import com.fiberhome.fp.pojo.FpOperationTable;
import com.fiberhome.fp.util.Page;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
public interface FpOperationTableDao {

    List<FpOperationTable> list(Page page,FpOperationTable fpOperationTable);
}
