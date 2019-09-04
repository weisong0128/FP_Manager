package com.fiberhome.fp.service.impl;

import com.fiberhome.fp.dao.IndexDataDao;
import com.fiberhome.fp.service.IndexDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
@Service
public class IndexDataServiceImpl implements IndexDataService {
    @Resource
    IndexDataDao indexDataDao;
    @Override
    public Map<String, Object> authManageCount(Map<String, Object> map) {
        return indexDataDao.authManageCount(map);
    }

    @Override
    public Map<String, Object> authManageCountByProv(Map<String, Object> map) {
        return indexDataDao.authManageCountByProv(map);
    }

    @Override
    public List<Map<String, Object>> authManageTOP10(Map<String, Object> map) {
        return indexDataDao.authManageTOP10(map);
    }

    @Override
    public List<Map<String, Object>> authManageTOP5(Map<String, Object> map) {
        return indexDataDao.authManageTOP5(map);
    }

    @Override
    public List<Map<String, Object>> openAuthManage(Map<String, Object> map) {
        return indexDataDao.openAuthManage(map);
    }

    @Override
    public List<Map<String, Object>> getAllauthManage(Map<String, Object> map) {
        return indexDataDao.getAllauthManage(map);
    }
}
