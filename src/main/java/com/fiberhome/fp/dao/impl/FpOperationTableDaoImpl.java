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


    @Override
    public List<FpOperationTable> list(Page page, FpOperationTable fpOperationTable) {
        StringBuilder sql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        sql.append(" select  date,errcode,errinfo,pjname,pjlocation from fp_operation_table  WHERE   syskv='nothing:1' ");
        countSql.append("select  count(*) as count  from fp_operation_table  WHERE  syskv='nothing:1' ");
        Map paramMap = new HashMap();
        if (StringUtils.isNotEmpty(fpOperationTable.getTimeTag())){
            if (StringUtils.equals("today",fpOperationTable.getTimeTag())){
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition",TimeUtil.partitons("today"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(0));
            }
            if (StringUtils.equals("seven",fpOperationTable.getTimeTag())){
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition",TimeUtil.partitons("seven"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date",TimeUtil.beforeFewDays(7));
            }
            if (StringUtils.equals("halfMonth",fpOperationTable.getTimeTag())){
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition",TimeUtil.partitons("halfMonth"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date",TimeUtil.beforeFewDays(15));
            }
            if (StringUtils.equals("all",fpOperationTable.getTimeTag())){
                sql.append(" and partition like '%' ");
                countSql.append(" and partition like '%' ");
            }
        }
        String errLevel =fpOperationTable.getErrLevel();
        if (errLevel != null && errLevel !=""){
            List<String> errLevels = EntityMapTransUtils.StringToList(fpOperationTable.getErrLevel());
            for (int i = 0; i < errLevels.size(); i++) {
                if (i==0){
                    sql.append(" and (errinfo  like '["+errLevels.get(i)+"-'");
                    countSql.append(" and errinfo like '["+errLevels.get(i)+"-'");
                }else {
                    sql.append(" or errinfo  like '["+errLevels.get(i)+"-'");
                    countSql.append(" or errinfo like '["+errLevels.get(i)+"-'");
                }
            }
            sql.append(")");
        }
        if (fpOperationTable.getPjNameList() != null && fpOperationTable.getPjNameList().size()>0 && !fpOperationTable.getPjNameList().contains("all")){
            sql.append(" and  pjname in (:pjName)");
            countSql.append(" and  pjname in (:pjName)");
            paramMap.put("pjName",fpOperationTable.getPjNameList());
        }
        if (fpOperationTable.getPjLocationList() != null && fpOperationTable.getPjLocationList().size()>0 && !fpOperationTable.getPjLocationList().contains("all")){
            sql.append(" and  pjlocation in (:pjLocation)");
            countSql.append(" and  pjlocation in (:pjLocation)");
            paramMap.put("pjLocation",fpOperationTable.getPjLocationList());
        }
        if (fpOperationTable.getStartTime()!=null&&fpOperationTable.getStartTime()!=""&&fpOperationTable.getEndTime()!=""&&fpOperationTable.getEndTime()!=""){
            sql.append(" and date like '["+fpOperationTable.getStartTime()+" TO "+fpOperationTable.getEndTime()+" ]' ");
            countSql.append(" and date like '["+fpOperationTable.getStartTime()+" TO "+fpOperationTable.getEndTime()+"]' ");
            paramMap.put("startTime",fpOperationTable.getStartTime());
            paramMap.put("endTime",fpOperationTable.getEndTime());
        }
        if (page != null){
            int total  = 0;
            List<FpOperationTable> count = namedParameterJdbcTemplate.query(countSql.toString(),paramMap,new BeanPropertyRowMapper<>(FpOperationTable.class));
            if (count != null && count.size()>0 && count.get(0).getCount() != null){
                total = count.get(0).getCount();
            }
            page.setTotalRows(total);
            sql.append(" ORDER BY date DESC limit "+page.getRowStart()+","+page.getPageSize());
        }
        List fpOperationTables = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(FpOperationTable.class));
        for (int i = 0; i < fpOperationTables.size(); i++) {
            FpOperationTable table = (FpOperationTable)fpOperationTables.get(i);
            errLevel = table.getErrInfo().substring(table.getErrInfo().indexOf("[")+1, table.getErrInfo().indexOf("-")+1);
            if ("CRIT-".equals(errLevel))errLevel="重度";
            if ("ERRO-".equals(errLevel))errLevel="中度";
            if ("WARN-".equals(errLevel))errLevel="轻度";
            if ("INFO-".equals(errLevel))errLevel=" 环境状态";
            table.setErrLevel(errLevel);
        }
        return fpOperationTables;
    }


}
