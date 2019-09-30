package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.FpProjectDao;
import com.fiberhome.fp.pojo.FpProject;
import com.fiberhome.fp.util.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */

@Repository
public class FpProjectDaoImpl implements FpProjectDao{

    @Autowired
    @Qualifier("hiveNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public List<FpProject> listProject(String pjName, Page page){
        Map<String,Object> map = new HashMap<>();
        StringBuilder sql = new StringBuilder("select pjname,pjlocation from fp_project where  pjname is not null ");
        StringBuilder sqlCount = new StringBuilder(" select count(*) as totalrows from fp_project where  pjname is not null ");
        if (StringUtils.isNotBlank(pjName)){
            map.put("pjName",pjName);
            sql.append(" AND  pjname = :pjName ");
            sqlCount.append(" AND  pjname = :pjName ");
        }
        if(page!=null){
            List<Map<String, Object>> count = namedParameterJdbcTemplate.queryForList(sqlCount.toString(),map);
            if(count.size()>0&&count.get(0).get("totalrows")!=null){
                int total= Integer.valueOf(count.get(0).get("totalrows").toString());
                page.setTotalRows(total);
            }
            sql.append("  limit "+page.getRowStart()+","+page.getPageSize());
        }
        return namedParameterJdbcTemplate.query(sql.toString(),map,new BeanPropertyRowMapper<>(FpProject.class));
    }

}
