package com.fiberhome.fp.service;

import com.fiberhome.fp.pojo.FpHelp;
import com.fiberhome.fp.util.Page;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */
public interface FpHelpService {

    /**
     * 小叮当
     * @return
     */
    List<FpHelp> getHelp(Page page, String errKeyWord);
}
