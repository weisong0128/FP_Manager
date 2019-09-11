package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.FpProjectDao;
import com.fiberhome.fp.pojo.FpProject;
import com.fiberhome.fp.util.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
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

    @Resource(name = "hiveJdbcTemplate")
    JdbcTemplate jdbcTemplate;


    @Override
    public List<FpProject> listProject(String pjName, Page page){
      Object[] obj = new Object[1];
        StringBuilder sql = new StringBuilder("select pjname,pjlocation from fp_project where  pjname <> 'null' ");
        StringBuilder sqlCount = new StringBuilder(" select count(*) as totalrows from fp_project where  pjname <> 'null' ");
        if (StringUtils.isNotBlank(pjName)){
            obj[0]=pjName;
            sql.append("AND  pjname = ? ");
            sqlCount.append("AND  pjname = ?");
        }
        if(page!=null){
            List<Map<String, Object>> count = jdbcTemplate.queryForList(sqlCount.toString(), obj);
            if(count.get(0).get("totalrows")!=null){
                int total= Integer.valueOf(count.get(0).get("totalrows").toString());
                page.setTotalRows(total);
            }
            sql.append("  limit "+page.getRowStart()+","+page.getPageSize());
        }
        return jdbcTemplate.query(sql.toString(),obj,new BeanPropertyRowMapper<>(FpProject.class));
    }

}
