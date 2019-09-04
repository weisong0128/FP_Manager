package com.fiberhome.fp.dao;

import com.fiberhome.fp.pojo.FpHelp;
import com.fiberhome.fp.util.Page;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
public interface FpHelpDao {

    /**
     * 根据错误编码或者错误原因查询帮助内容
     * @return
     */
    List<FpHelp> getFpHelp(Page page, String errKeyWord);
}
