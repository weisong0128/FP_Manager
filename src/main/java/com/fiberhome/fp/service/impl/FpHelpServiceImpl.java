package com.fiberhome.fp.service.impl;

import com.fiberhome.fp.dao.FpHelpDao;
import com.fiberhome.fp.pojo.FpHelp;
import com.fiberhome.fp.service.FpHelpService;
import com.fiberhome.fp.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */

@Service
public class FpHelpServiceImpl implements FpHelpService {

    @Autowired
    FpHelpDao fpHelpDao;

    @Override
    public List<FpHelp> getHelp(Page page, String errKeyWord) {
        return fpHelpDao.getFpHelp(page,errKeyWord);
    }
}
