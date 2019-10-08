package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.FpOperationTableDao;
import com.fiberhome.fp.pojo.FpOperationTable;
import com.fiberhome.fp.util.EntityMapTransUtils;
import com.fiberhome.fp.util.Page;
import com.fiberhome.fp.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */

@Repository
public class FpOperationTableDaoImpl implements FpOperationTableDao {


    @Autowired
    @Qualifier("hiveNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    private  final  int DAYS_7=7;
    private  final  int DAYS_15=15;
    @Override
    public List<FpOperationTable> list(Page page, FpOperationTable fpOperationTable) {
        StringBuilder sql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        sql.append(" select  ");
        if (fpOperationTable.getIsDistinct()==1){
            sql.append(" max(date) as date, ");
        }else {
            sql.append(" date, ");
        }
        sql.append(" errcode,errinfo,pjname,pjlocation from fp_operation_table  WHERE   syskv='nothing:1' ");
        Map paramMap = new HashMap();
        if (StringUtils.isNotEmpty(fpOperationTable.getTimeTag())){
            if (StringUtils.equals("today",fpOperationTable.getTimeTag())){
                sql.append(" and partition in (:partition) ");
                paramMap.put("partition",TimeUtil.partitons("today"));
                sql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(0));
            }
            if (StringUtils.equals("seven",fpOperationTable.getTimeTag())){
                sql.append(" and partition in (:partition) ");
                paramMap.put("partition",TimeUtil.partitons("seven"));
                sql.append(" and date > :date ");
                paramMap.put("date",TimeUtil.beforeFewDays(DAYS_7));
            }
            if (StringUtils.equals("halfMonth",fpOperationTable.getTimeTag())){
                sql.append(" and partition in (:partition) ");
                paramMap.put("partition",TimeUtil.partitons("halfMonth"));
                sql.append(" and date > :date ");
                paramMap.put("date",TimeUtil.beforeFewDays(DAYS_15));
            }
            if (StringUtils.equals("all",fpOperationTable.getTimeTag())){
                sql.append(" and partition like '%' ");
            }
        }
        String errLevel =fpOperationTable.getErrLevel();
        if (StringUtils.isNotBlank(errLevel)){
            List<String> errLevels = EntityMapTransUtils.StringToList(fpOperationTable.getErrLevel());
            for (int i = 0; i < errLevels.size(); i++) {
                if (i==0){
                    sql.append(" and ( SEARCH_ALL= 'errinfo@["+errLevels.get(i)+"'");
                }else {
                    sql.append(" or SEARCH_ALL= 'errinfo@["+errLevels.get(i)+"'");
                }
            }
            sql.append(")");
        }
        if (fpOperationTable.getPjNameList() != null && fpOperationTable.getPjNameList().size()>0 && !fpOperationTable.getPjNameList().contains("all")){
            sql.append(" and  pjname in (:pjName)");
            paramMap.put("pjName",fpOperationTable.getPjNameList());
        }
        if (fpOperationTable.getCaptureTime()!=null){
            sql.append(" and  capture_time in (:captureTime)");
            paramMap.put("captureTime",fpOperationTable.getCaptureTime());
        }
        if (fpOperationTable.getPjLocationList() != null && fpOperationTable.getPjLocationList().size()>0 && !fpOperationTable.getPjLocationList().contains("all")){
            sql.append(" and  pjlocation in (:pjLocation)");
            paramMap.put("pjLocation",fpOperationTable.getPjLocationList());
        }
        if (StringUtils.isNotBlank(fpOperationTable.getStartTime())&&StringUtils.isNotBlank(fpOperationTable.getEndTime())){
            sql.append(" and date like '["+fpOperationTable.getStartTime()+" TO "+fpOperationTable.getEndTime()+" ]' ");
            paramMap.put("startTime",fpOperationTable.getStartTime());
            paramMap.put("endTime",fpOperationTable.getEndTime());
        }

        if (StringUtils.isNotBlank(fpOperationTable.getKeyWord())){
            sql.append(" and    SEARCH_ALL=:keyword ");
            paramMap.put("keyword",fpOperationTable.getKeyWord());
        }

        if(fpOperationTable.getIsDistinct()==1){
            sql.append(" group by date,errcode,errinfo,pjname,pjlocation ");
        }

        if (StringUtils.isNotBlank(fpOperationTable.getSortName())){
            sql.append(" ORDER BY ");
            if ("date".equals(fpOperationTable.getSortName())){
                sql.append(" date ");
            }
            if ("desc".equals(fpOperationTable.getSort())){
                sql.append(" desc ");
            }
            if ("asc".equals(fpOperationTable.getSort())){
                sql.append(" asc ");
            }
        }
        countSql.append(" select count(*) as count from ("+sql+")A");
        if (page != null){
            int total  = 0;
            List<FpOperationTable> count = namedParameterJdbcTemplate.query(countSql.toString(),paramMap,new BeanPropertyRowMapper<>(FpOperationTable.class));
            if (count != null && count.size()>0 && count.get(0).getCount() != null){
                total = count.get(0).getCount();
            }
            page.setTotalRows(total);
            sql.append("  limit "+page.getRowStart()+","+page.getPageSize());
        }
        List fpOperationTables = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(FpOperationTable.class));
        for (int i = 0; i < fpOperationTables.size(); i++) {
            FpOperationTable table = (FpOperationTable)fpOperationTables.get(i);
            errLevel = table.getErrInfo().substring(table.getErrInfo().indexOf("[")+1, table.getErrInfo().indexOf("-")+1);
            if ("CRIT-".equals(errLevel)){errLevel="重度";}
            if ("ERRO-".equals(errLevel)){errLevel="中度";}
            if ("WARN-".equals(errLevel)){errLevel="轻度";}
            if ("INFO-".equals(errLevel)){errLevel=" 环境状态";}
            table.setErrLevel(errLevel);
        }
        return fpOperationTables;
    }


}
