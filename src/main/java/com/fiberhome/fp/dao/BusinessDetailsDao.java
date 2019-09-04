package com.fiberhome.fp.dao;

import com.fiberhome.fp.pojo.BusinessDetails;
import com.fiberhome.fp.util.Page;

import java.util.List;
import java.util.Map;

public interface BusinessDetailsDao {
    List<BusinessDetails>  getBusinessDetails(Map<String,Object> parames,Page page);
}
