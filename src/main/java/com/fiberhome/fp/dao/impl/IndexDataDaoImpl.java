package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.IndexDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Repository
public class IndexDataDaoImpl implements IndexDataDao {
    @Autowired
    @Qualifier("mysqlNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Resource(name = "mysqlJdbcTemplate")
    JdbcTemplate jdbcTemplate;


    @Override
    public  Map<String, Object> authManageCount(Map<String, Object> map) {
       /* StringBuilder sql = new StringBuilder("SELECT COUNT(uuid)  AS envirNote, COUNT(DISTINCT cities) as  cities,COUNT(DISTINCT project_name) as projectName  FROM fp_auth_management WHERE 1=1 ");
        if (map.get("envirNote")!=null){
            sql= new StringBuilder("SELECT COUNT(envir_note)  AS envirNote  FROM fp_auth_management WHERE 1=1  ");
            sql.append(" and envir_note = :envirNote ");
        }*/
       StringBuilder sql = new StringBuilder(" SELECT\n" +
               "\tCOUNT( * ) AS uuidCount,\n" +
               "\tCOUNT( envir_note = '0' OR NULL ) AS envirNote1,\n" +
               "\tCOUNT( envir_note = '1' OR NULL ) AS envirNote2,\n" +
               "\tCOUNT( DISTINCT cities ) AS cities,\n" +
               "\tCOUNT( DISTINCT project_name ) AS projectnName \n" +
               "FROM\n" +
               "\tfp_auth_management \n" +
               "WHERE\n" +
               "\tis_available != '1' and envir_note !='3' ");
        Map<String, Object> map1 = namedParameterJdbcTemplate.queryForMap(sql.toString(), map);
        return  map1;
    }

    @Override
    public Map<String, Object> authManageCountByProv(Map<String, Object> map) {
        StringBuilder sql = new StringBuilder("SELECT A.envirNote1,B.envirNote2 FROM ( SELECT COUNT(envir_note) as envirNote1  FROM fp_auth_management WHERE envir_note = '0' and is_available !='1' and  provinces =:provinces) A,( SELECT COUNT(envir_note) as envirNote2 FROM fp_auth_management WHERE envir_note = '1' and is_available !='1'  and  provinces=:provinces) B ");
        Map<String, Object> map1 = namedParameterJdbcTemplate.queryForMap(sql.toString(), map);
        return map1;
    }

    @Override
    public List<Map<String, Object>> authManageTOP10(Map<String, Object> map) {
        StringBuilder sql = new StringBuilder("select project_name, count(*) as cnt from fp_auth_management WHERE  envir_note = '0' and is_available !='1'  group  by project_name order by cnt desc LIMIT 10 ");
        List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(sql.toString(), map);
        return maps;
    }

    @Override
    public List<Map<String, Object>> authManageTOP5(Map<String, Object> map) {
        StringBuilder sql = new StringBuilder("SELECT cities , count(cities) as citiesCount ,count(envir_note='0' or null) as productionCount,count(envir_note='1' or null) as testCount FROM fp_auth_management WHERE 1=1 and cities is not null and is_available !='1' and envir_note !='3'  GROUP BY cities  ORDER BY citiesCount DESC LIMIT 5");
        List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(sql.toString(), map);
        return maps;
    }

    @Override
    public List<Map<String, Object>> openAuthManage(Map<String, Object> map) {
        map.put("year", Calendar.getInstance().get(Calendar.YEAR));
        map.put("year1", Calendar.getInstance().get(Calendar.YEAR)-1);
        StringBuilder sql = new StringBuilder("SELECT  DATE_FORMAT( download_time, '%m' ) AS month, COUNT(*) AS count ,DATE_FORMAT( download_time, '%Y' ) AS year   FROM fp_auth_management WHERE 1=1 and envir_note !='3' and is_available ='0' and DATE_FORMAT( download_time, '%Y' ) in(:year,:year1) GROUP BY MONTH ORDER BY YEAR ,MONTH");
        List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(sql.toString(), map);
        return maps;
    }

    @Override
    public List<Map<String, Object>> getAllauthManage(Map<String, Object> map) {
        String sql = "SELECT  count((envir_note = '0' or  null)) as onlinecount,count((envir_note = '1' or  null))as testcount,provinces   FROM fp_auth_management WHERE  is_available !='1' and envir_note !='3' and envir_note is not null GROUP BY provinces";
        return namedParameterJdbcTemplate.queryForList(sql,map);
    }
}
