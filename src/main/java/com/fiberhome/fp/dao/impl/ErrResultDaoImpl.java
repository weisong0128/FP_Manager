package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.ErrResultDao;
import com.fiberhome.fp.pojo.AllResult;
import com.fiberhome.fp.pojo.ErrorResult;
import com.fiberhome.fp.util.EntityMapTransUtils;
import com.fiberhome.fp.util.Page;
import com.fiberhome.fp.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */

@Repository
public class ErrResultDaoImpl implements ErrResultDao {


    @Autowired
    @Qualifier("hiveNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    private  final  int DAYS_7=7;
    private  final  int DAYS_15=15;
    @Override
    public List<ErrorResult> ListErrResult(Page page, ErrorResult errorResult) {

        StringBuilder sql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        sql.append(" select  date,tag,alter_tag,sql_result,pjname,pjlocation from err_result  WHERE   syskv='nothing:1' ");
        countSql.append("select  count(*) as count  from err_result  WHERE   syskv='nothing:1' ");
        Map paramMap = new HashMap();
        if (StringUtils.isNotEmpty(errorResult.getTimeTag())){
            if (StringUtils.equals("today",errorResult.getTimeTag())){
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition",TimeUtil.partitons("today"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(0));
            }
            if (StringUtils.equals("seven",errorResult.getTimeTag())){
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition",TimeUtil.partitons("seven"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date",TimeUtil.beforeFewDays(DAYS_7));
            }
            if (StringUtils.equals("halfMonth",errorResult.getTimeTag())){
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition",TimeUtil.partitons("halfMonth"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date",TimeUtil.beforeFewDays(DAYS_15));
            }
            if (StringUtils.equals("all",errorResult.getTimeTag())){
                sql.append(" and partition like '%' ");
                countSql.append(" and partition like '%' ");
            }
        }

        if (errorResult.getPjNameList() != null && errorResult.getPjNameList().size()>0 && !errorResult.getPjNameList().contains("all")){
            sql.append(" and  pjname in (:pjName)");
            countSql.append(" and  pjname in (:pjName)");
            paramMap.put("pjName",errorResult.getPjNameList());
        }
        if (errorResult.getPjLocationList() != null && errorResult.getPjLocationList().size()>0 && !errorResult.getPjLocationList().contains("all")){
            sql.append(" and  pjlocation in (:pjLocation)");
            countSql.append(" and  pjlocation in (:pjLocation)");
            paramMap.put("pjLocation",errorResult.getPjLocationList());
        }
        if (StringUtils.isNotBlank(errorResult.getStartTime())&&StringUtils.isNotBlank(errorResult.getEndTime())){
            sql.append(" and date like '["+errorResult.getStartTime()+" TO "+errorResult.getEndTime()+" ]' ");
            countSql.append(" and date like '["+errorResult.getStartTime()+" TO "+errorResult.getEndTime()+"]' ");
            paramMap.put("startTime",errorResult.getStartTime());
            paramMap.put("endTime",errorResult.getEndTime());
        }
        if (StringUtils.isNotBlank(errorResult.getKeyWord())){
            sql.append(" and  SEARCH_ALL=:keyword ");
            countSql.append(" and  SEARCH_ALL=:keyword  ");
            paramMap.put("keyword",errorResult.getKeyWord());
        }

        if (StringUtils.isNotBlank(errorResult.getErrorSqlType())){
            List<String> errorSqlType = EntityMapTransUtils.StringToList(errorResult.getErrorSqlType());
            sql.append(" and  SEARCH_ALL in (:errorSqlType) ");
            countSql.append(" and  SEARCH_ALL in(:errorSqlType)  ");
            paramMap.put("errorSqlType",errorSqlType);
        }

        sql.append(" ORDER BY ");
        if (StringUtils.isNotBlank(errorResult.getSortName())){
            if ("date".equals(errorResult.getSortName())){
                sql.append(" date ");
            }
            if ("desc".equals(errorResult.getSort())){
                sql.append(" desc ");
            }
            if ("asc".equals(errorResult.getSort())){
                sql.append(" asc ");
            }
        }

        if (page != null){
            int total  = 0;
            List<ErrorResult> count = namedParameterJdbcTemplate.query(countSql.toString(),paramMap,new BeanPropertyRowMapper<>(ErrorResult.class));
            if (count != null && count.size()>0 && count.get(0).getCount()!= null){
                total = count.get(0).getCount();
            }
            page.setTotalRows(total);
            sql.append("  limit "+page.getRowStart()+","+page.getPageSize());
        }
        return namedParameterJdbcTemplate.query(sql.toString(),paramMap,new BeanPropertyRowMapper<>(ErrorResult.class));

    }

}
