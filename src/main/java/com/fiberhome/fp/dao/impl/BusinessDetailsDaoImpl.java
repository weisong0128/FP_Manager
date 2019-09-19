package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.BusinessDetailsDao;
import com.fiberhome.fp.pojo.BusinessDetails;
import com.fiberhome.fp.pojo.FpHelp;
import com.fiberhome.fp.pojo.RowResult;
import com.fiberhome.fp.util.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class BusinessDetailsDaoImpl implements BusinessDetailsDao {

    @Autowired
    @Qualifier("hiveNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<BusinessDetails> getBusinessDetails(Map<String, Object> parames, Page page) {
        StringBuilder sql = new StringBuilder("select table_name,count(*) as cnt,pjlocation,max(date) as date from sql_tmp where  tag='fp_table' ");
        if(parames.get("pjLocations")!=null && parames.get("pjLocations")!=""){
            sql.append(" AND pjlocation in( :pjLocations )  ");
        }
        if (parames.get("pjName")!=null && parames.get("pjName")!=""){
            sql.append(" AND pjname = :pjName  ");
        }
        //根据时间筛选
        if (parames.get("partition")!=null&& parames.get("partition")!=""){
            sql.append(" AND partition in (:partition)  ");
        }

        if (parames.get("tableName")!=null&& parames.get("tableName")!=""){
            sql.append(" AND table_name in (:tableName)  ");
        }
        sql.append(" group by table_name,pjlocation order by ");//cnt desc limit "+page.getRowStart()+","+page.getPageSize());
        if (StringUtils.isNotBlank((String) parames.get("sortName"))){
            if ("cnt".equals(parames.get("sortName"))){
                sql.append(" cnt ");
            }
            if ("date".equals(parames.get("sortName"))){
                sql.append(" date ");
            }
            if ("desc".equals(parames.get("sort"))){
                sql.append(" desc ");
            }
            if ("asc".equals(parames.get("sort"))){
                sql.append(" asc ");
            }
        }
        sql.append(" limit "+page.getRowStart()+","+page.getPageSize());

        if (page!=null){
            StringBuilder   totalRows = new StringBuilder(" select count(*) as totalrows from (select table_name,count(*) as cnt,pjlocation,max(date) as date from sql_tmp where  tag='fp_table'  ");
            if(parames.get("pjLocations")!=null && parames.get("pjLocations")!=""){
                totalRows.append(" AND pjlocation in( :pjLocations )  ");
            }
            if (parames.get("pjName")!=null && parames.get("pjName")!=""){
                totalRows.append(" AND pjname = :pjName  ");
            }
            //根据时间筛选
            if (parames.get("partition")!=null&& parames.get("partition")!=""){
                totalRows.append(" AND partition in (:partition)  ");
            }
            if (parames.get("tableName")!=null&& parames.get("tableName")!=""){
                totalRows.append(" AND table_name in (:tableName)  ");
            }
            totalRows.append("  group by table_name,pjlocation order by cnt desc) A   ");
            List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(totalRows.toString(), parames);
            if(maps.get(0).get("totalrows")!=null){
                page.setTotalRows(Integer.valueOf(maps.get(0).get("totalrows").toString()));
            }
        }
        List<BusinessDetails> query = namedParameterJdbcTemplate.query(sql.toString(), parames, new BeanPropertyRowMapper<>(BusinessDetails.class));
        return query;
    }
}
