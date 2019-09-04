package com.fiberhome.fp.service;

import com.fiberhome.fp.pojo.BusinessDetails;
import com.fiberhome.fp.util.Page;

import java.util.List;
import java.util.Map;

public interface BusinessDetailsService {

    List<BusinessDetails>  getBusinessDetails(Map<String,Object> parames, Page page);
}
