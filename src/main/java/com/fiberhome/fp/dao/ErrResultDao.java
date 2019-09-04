package com.fiberhome.fp.dao;

import com.fiberhome.fp.pojo.ErrorResult;
import com.fiberhome.fp.util.Page;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
public interface ErrResultDao {

    List<ErrorResult> ListErrResult(Page page,ErrorResult errorResult);
}
