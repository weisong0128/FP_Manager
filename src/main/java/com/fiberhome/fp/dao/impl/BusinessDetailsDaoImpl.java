package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.BusinessDetailsDao;
import com.fiberhome.fp.pojo.BusinessDetails;
import com.fiberhome.fp.pojo.FpHelp;
import com.fiberhome.fp.pojo.RowResult;
import com.fiberhome.fp.util.Page;
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
        StringBuilder sql = new StringBuilder("SELECT table_name,count(*) as count,table_partition,pjlocation,partition,capture_time FROM table_tmp where partition like :pjName ");
        if(parames.get("pjLocations")!=null && parames.get("pjLocations")!=""){
            sql.append(" AND pjlocation in( :pjLocations )  ");
        }
        //根据时间筛选
    /*    if (parames.get("tablePartitions")!=null&& parames.get("tablePartitions")!=""){
            sql.append(" AND table_partition in (:tablePartitions)  ");
        }*/
        if (parames.get("tableName")!=null&& parames.get("tableName")!=""){
            sql.append(" AND table_name in (:tableName)  ");
        }


        sql.append(" GROUP BY table_name,table_partition,pjlocation,partition,capture_time ORDER BY count desc limit "+page.getRowStart()+","+page.getPageSize());
        if (page!=null){
            StringBuilder   totalRows = new StringBuilder("SELECT count(*) as totalrows FROM table_tmp  where partition like :pjName  ");
                if(parames.get("pjLocations")!=null && parames.get("pjLocations")!=""){
                    totalRows.append(" AND pjlocation in( :pjLocations )  ");
                }
                if (parames.get("tablePartitions")!=null && parames.get("tablePartitions")!=""){
                    totalRows.append(" AND table_partition in (:tablePartitions)  ");
                }
            List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(totalRows.toString(), parames);
            if(maps.get(0).get("totalrows")!=null){
                page.setTotalRows(Integer.valueOf(maps.get(0).get("totalrows").toString()));
            }
        }
        List<BusinessDetails> query = namedParameterJdbcTemplate.query(sql.toString(), parames, new BeanPropertyRowMapper<>(BusinessDetails.class));
        return query;
    }
}
