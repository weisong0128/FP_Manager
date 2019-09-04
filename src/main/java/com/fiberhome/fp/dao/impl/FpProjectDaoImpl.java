package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.FpProjectDao;
import com.fiberhome.fp.pojo.FpProject;
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
    public List<FpProject> listProject(String pjName){
      Object[] obj = new Object[1];
        StringBuilder sql = new StringBuilder("select pjname,pjlocation from fp_project where  pjname <> 'null' ");
        if (pjName!=null && pjName!=""){
            obj[0]=pjName;
            sql.append("AND  pjname = ? ");
        }
        sql.append(" limit 1000 ");
        return jdbcTemplate.query(sql.toString(),obj,new BeanPropertyRowMapper<>(FpProject.class));
    }

}
