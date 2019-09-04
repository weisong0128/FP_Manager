package com.fiberhome.fp.service.impl;

import com.fiberhome.fp.dao.impl.BusinessDetailsDaoImpl;
import com.fiberhome.fp.pojo.BusinessDetails;
import com.fiberhome.fp.service.BusinessDetailsService;
import com.fiberhome.fp.util.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class BusinessDetailsServiceImpl implements BusinessDetailsService {
    @Resource
    BusinessDetailsDaoImpl businessDetailsDao;
    @Override
    public List<BusinessDetails> getBusinessDetails(Map<String, Object> parames, Page page) {
        return businessDetailsDao.getBusinessDetails(parames,page);
    }
}
