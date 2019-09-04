package com.fiberhome.fp.service.impl;

import com.fiberhome.fp.dao.ErrResultDao;
import com.fiberhome.fp.dao.FpOperationTableDao;
import com.fiberhome.fp.pojo.ErrorResult;
import com.fiberhome.fp.pojo.FpOperationTable;
import com.fiberhome.fp.service.ErrorResultService;
import com.fiberhome.fp.util.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */

@Service
public class ErrorResultServiceImpl implements ErrorResultService {

    @Autowired
    ErrResultDao errResultDao;

    @Autowired
    FpOperationTableDao fpOperationTableDao;


    @Override
    public List<ErrorResult> listErrorResult(Page page,ErrorResult errorResult) {
        if (StringUtils.isNotEmpty(errorResult.getPjName())){
            String[] pjNames = errorResult.getPjName().split(",");
            errorResult.setPjNameList(new ArrayList<>(Arrays.asList(pjNames)));
        }
        if (StringUtils.isNotEmpty(errorResult.getPjLocation())){
            String[] pjLocations = errorResult.getPjLocation().split(",");
            errorResult.setPjLocationList(new ArrayList<>(Arrays.asList(pjLocations)));
        }
        return errResultDao.ListErrResult(page,errorResult);
    }

    @Override
    public List<FpOperationTable> listOperation(Page page, FpOperationTable fpOperationTable) {
        if (StringUtils.isNotEmpty(fpOperationTable.getPjName())){
            String[] pjNames = fpOperationTable.getPjName().split(",");
            fpOperationTable.setPjNameList(new ArrayList<>(Arrays.asList(pjNames)));
        }
        if (StringUtils.isNotEmpty(fpOperationTable.getPjLocation())){
            String[] pjLocations = fpOperationTable.getPjLocation().split(",");
            fpOperationTable.setPjLocationList(new ArrayList<>(Arrays.asList(pjLocations)));
        }
        return fpOperationTableDao.list(page,fpOperationTable);
    }

}
