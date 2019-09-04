package com.fiberhome.fp.service.impl;

import com.fiberhome.fp.dao.AllResultDao;
import com.fiberhome.fp.pojo.AllResult;
import com.fiberhome.fp.pojo.RowResult;
import com.fiberhome.fp.service.AllResultService;
import com.fiberhome.fp.util.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/2
 */
@Service
public class AllResultServiceImpl implements AllResultService {


    @Autowired
    AllResultDao allResultDao;

    @Override
    public List<AllResult> getAllResult(Page page, AllResult allResult) {

        if (StringUtils.isNotEmpty(allResult.getPjName())){
            String[] pjNames = allResult.getPjName().split(",");
            allResult.setPjNameList(new ArrayList<>(Arrays.asList(pjNames)));
        }
        if (StringUtils.isNotEmpty(allResult.getPjLocation())){
            String[] pjLocations = allResult.getPjLocation().split(",");
            allResult.setPjLocationList(new ArrayList<>(Arrays.asList(pjLocations)));
        }
        if(StringUtils.isNotEmpty(allResult.getTag())){
            String[] tags = allResult.getTag().split(",");
            allResult.setTagList(new ArrayList<>(Arrays.asList(tags)));
        }

        return allResultDao.listAllResult(page,allResult);
    }



    @Override
    public AllResult getProportionDate(String pjName, String pJLocation,String time,String startTime,String endTime) {
        List<String> pjNameList = new ArrayList<>();
        List<String> pjLocationList = new ArrayList<>();
        if (StringUtils.isNotEmpty(pjName)){
            String[] pjNames = pjName.split(",");
            pjNameList = new ArrayList<>(Arrays.asList(pjNames));
        }
        if (StringUtils.isNotEmpty(pJLocation)){
            String[] pjLocations = pJLocation.split(",");
            pjLocationList = new ArrayList<>(Arrays.asList(pjLocations));
        }
        return allResultDao.getProportion(pjNameList,pjLocationList,time,startTime,endTime);
    }

    @Override
    public List<RowResult> rowResultList(Page page, RowResult rowResult) {
        return allResultDao.rowResultList(page,rowResult);
    }

    @Override
    public List<AllResult> tagList() {
        return allResultDao.tagList();
    }
}
