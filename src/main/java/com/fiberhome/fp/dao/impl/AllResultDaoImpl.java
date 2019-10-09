package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.AllResultDao;
import com.fiberhome.fp.pojo.AllResult;
import com.fiberhome.fp.pojo.RowResult;
import com.fiberhome.fp.util.Page;
import com.fiberhome.fp.util.TimeUtil;
import com.fiberhome.fp.vo.ErrorSqlCount;
import com.fiberhome.fp.vo.SqlCount;
import com.fiberhome.fp.vo.TagProporation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.Month;
import java.util.*;

/**
 * @author fengxiaochun
 * @date 2019/7/3
 */

@Repository
public class AllResultDaoImpl implements AllResultDao {

    @Resource(name = "hiveJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("hiveNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    private final int DAYS_7 = 7;
    private final int DAYS_15 = 15;

    @Override
    public List<AllResult> listAllResult(Page page, AllResult allResult) {
        StringBuilder sql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        sql.append(" select  ");
       // System.err.println(allResult.getIsDistinct());
        if (allResult.getIsDistinct()==1){
            sql.append(" max(date) as date, ");
        }else {
            sql.append(" date, ");
        }
        sql.append(" time,tag,sql_result,pjname,pjlocation  from all_result  WHERE   syskv='nothing:1' ");
        Map paramMap = new HashMap();
        if (StringUtils.isNotEmpty(allResult.getTimeTag())) {
            if (StringUtils.equals("today", allResult.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons("today"));
                sql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(0));
            }
            if (StringUtils.equals("seven", allResult.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons("seven"));
                sql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(DAYS_7));
            }
            if (StringUtils.equals("halfMonth", allResult.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons("halfMonth"));
                sql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(DAYS_15));
            }
            if (StringUtils.equals("all", allResult.getTimeTag())) {
                sql.append(" and partition like '%' ");
            }
        }
        if (allResult.getTagList() != null && allResult.getTagList().size() > 0 && !allResult.getTagList().contains("all")) {
            sql.append(" and tag in (:tag) ");
            paramMap.put("tag", allResult.getTagList());
        }
        if (StringUtils.isNotEmpty(allResult.getDuration()) && !allResult.getDuration().contains("all")) {
            String[] durations = allResult.getDuration().split(",");
            if (durations != null && durations.length > 0) {
                sql.append(" and ( ");
                for (int i = 0; i < durations.length; i++) {
                    jointSql(sql, durations[i], i);
                }
                sql.append(" ) ");
            }
        }
        if (allResult.getPjNameList() != null && allResult.getPjNameList().size() > 0 && !allResult.getPjNameList().contains("all")) {
            sql.append(" and  pjname in (:pjName)");
            paramMap.put("pjName", allResult.getPjNameList());
        }
        if (allResult.getPjLocationList() != null && allResult.getPjLocationList().size() > 0 && !allResult.getPjLocationList().contains("all")) {
            sql.append(" and  pjlocation in (:pjLocation)");
            paramMap.put("pjLocation", allResult.getPjLocationList());
        }
        if (StringUtils.isNotBlank(allResult.getStartTime()) && StringUtils.isNotBlank(allResult.getEndTime())) {
            sql.append(" and date like '[" + allResult.getStartTime() + " TO " + allResult.getEndTime() + " ]' ");
            paramMap.put("startTime", allResult.getStartTime());
            paramMap.put("endTime", allResult.getEndTime());
        }

        if (StringUtils.isNotBlank(allResult.getKeyWord())) {
            sql.append(" and  SEARCH_ALL=:keyword ");
            paramMap.put("keyword", allResult.getKeyWord());
        }

        if(allResult.getIsDistinct()==1){
            sql.append(" group by date,time,tag,sql_result,pjname,pjlocation ");
        }


        sql.append(" order by  ");
        if (StringUtils.isNotBlank(allResult.getSortName())) {
            if ("date".equals(allResult.getSortName())) {
                sql.append(" date ");
            }
            if ("time".equals(allResult.getSortName())) {
                sql.append(" time ");
            }
            if ("desc".equals(allResult.getSort())) {
                sql.append(" desc ");
            }
            if ("asc".equals(allResult.getSort())) {
                sql.append(" asc ");
            }
        }
        countSql.append(" select count(*) as count from ("+sql+")A");
        if (page != null) {
            int total = 0;
            List<AllResult> totalList = namedParameterJdbcTemplate.query(countSql.toString(), paramMap, new BeanPropertyRowMapper<>(AllResult.class));
            if (totalList != null && totalList.size() > 0 && totalList.get(0).getCount() != null) {
                total = totalList.get(0).getCount();
            }
            page.setTotalRows(total);
            sql.append(" limit " + page.getRowStart() + "," + page.getPageSize());
        }
        List<AllResult> list = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(AllResult.class));
        return list;
    }

    /**
     * \
     * 业务分析sql占比表格数据
     *
     * @return
     */
    @Override
    public AllResult getProportion(List<String> pjNames, List<String> pjLocations, String time, String startTime, String endTime) {
        List<String> partitions = partitions(time);
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            startTime = startTime + "000";
            endTime = endTime + "000";
            partitions = TimeUtil.convertMonth(Long.valueOf((startTime)), Long.valueOf((endTime)));
        }
        long date = TimeUtil.beforeFewMonthLong(partitions.size() - 1);
        AllResult allResult = new AllResult();
        Map<String, Object> param = new HashMap();
        //合格sql数量
//        String qualifiedSql = "select count(*) as count from all_result where partition in (:partition) and pjname=:pjName and pjlocation=:pjLocation and date >:date limit 1";
        StringBuilder qualifiedSql = new StringBuilder();
        qualifiedSql.append(" select count(*) as count from all_result where partition in (:partition) ");
        joinSql(pjNames, pjLocations, param, qualifiedSql);
        qualifiedSql.append(" and date >:date limit 1 ");
        //不合格sql数量
//        String unqualifiedSql = "select count(*) as count from err_result where partition  in (:partition) and pjname=:pjName and pjlocation=:pjLocation and date >:date limit 1";
        StringBuilder unqualifiedSql = new StringBuilder();
        unqualifiedSql.append(" select count(*) as count from err_result where partition  in (:partition) ");
        joinSql(pjNames, pjLocations, param, unqualifiedSql);
        unqualifiedSql.append(" and date >:date limit 1 ");
        //sql类型占比
//        String tagProporationSql = "select tag,count(*) as value from all_result where partition  in (:partition) and pjname=:pjName and pjlocation=:pjLocation and date >:date group by tag limit 10";
        StringBuilder tagProporationSql = new StringBuilder();
        tagProporationSql.append(" select tag,count(*) as value from all_result where partition  in (:partition) ");
        joinSql(pjNames, pjLocations, param, tagProporationSql);
        tagProporationSql.append(" and date >:date group by tag limit 10 ");


        //个时刻下发sql统计
//        String sqlCountSql = "select hour,count(*) as count from (select substr(date,12,2) as hour from " +
//                " (select from_unixtime(date) as date from " +
//                " (select date from all_result where partition  in (:partition)  and pjname=:pjName and pjlocation=:pjLocation and date >:date )t)a)s group by hour order by hour limit 30";

        StringBuilder sqlCountSql = new StringBuilder();
        sqlCountSql.append(" select hour,count(*) as count from (select substr(date,12,2) as hour from  " +
                " (select from_unixtime(date) as date from  " +
                " (select date from all_result where partition  in (:partition) ");
        joinSql(pjNames, pjLocations, param, sqlCountSql);
        sqlCountSql.append(" and date >:date )t)a)s group by hour order by hour limit 30 ");


//        String errorSqlcountSql = "select month,count(*) as count from (select substr(date,0,7) as month from " +
//                " (select date from fp_operation_table where partition like '%' and pjname=:pjName and pjlocation=:pjLocation and date >:date )t)a group by month order by  month limit 100";

        StringBuilder errorSqlcountSql = new StringBuilder();
        errorSqlcountSql.append(" select month,count(*) as count from (select substr(date,0,7) as month from  " +
                " (select from_unixtime(date) as date from " +
                " (select date from fp_operation_table where partition like '%' ");
        joinSql(pjNames, pjLocations, param, errorSqlcountSql);
        errorSqlcountSql.append(" and date >:date )t)a)s group by month order by  month limit 100 ");


//        param.put("pjName",pJName);
//        param.put("pjLocation",pJLocation);
        param.put("partition", partitions);
        param.put("date", date);


        List<AllResult> qualifiedCountList = namedParameterJdbcTemplate.query(qualifiedSql.toString(), param, new BeanPropertyRowMapper<>(AllResult.class));

        int qualifiedSqlCount = 0;
        if (qualifiedCountList != null && qualifiedCountList.size() > 0) {
            qualifiedSqlCount = qualifiedCountList.get(0).getCount();
        }
        List<AllResult> unqualifiedCountList = namedParameterJdbcTemplate.query(unqualifiedSql.toString(), param, new BeanPropertyRowMapper<>(AllResult.class));
        int unqualifiedSqlCount = 0;
        if (unqualifiedCountList != null && unqualifiedCountList.size() > 0) {
            unqualifiedSqlCount = unqualifiedCountList.get(0).getCount();
        }
        List<TagProporation> tagProporationList = namedParameterJdbcTemplate.query(tagProporationSql.toString(), param, new BeanPropertyRowMapper<>(TagProporation.class));
        List<SqlCount> sqlCountList = namedParameterJdbcTemplate.query(sqlCountSql.toString(), param, new BeanPropertyRowMapper<>(SqlCount.class));
        List<ErrorSqlCount> errorSqlCountList = namedParameterJdbcTemplate.query(errorSqlcountSql.toString(), param, new BeanPropertyRowMapper<>(ErrorSqlCount.class));
        allResult.setQualifiedSql(qualifiedSqlCount);
        allResult.setUnqualifiedSql(unqualifiedSqlCount);
        allResult.setTagProporationsList(tagProporationList);
        allResult.setSqlCountList(sqlCountList);
        allResult.setErrorSqlCountList(errorSqlCountList);
        return allResult;

    }


    @Override
    public List<RowResult> rowResultList(Page page, RowResult rowResult) {
     /*   List<String> pjNameList = new ArrayList<>();
        List<String> pjLocationList = new ArrayList<>();*/
       /* if (StringUtils.isNotEmpty(rowResult.getPjName())){
            String[] pjNames = rowResult.getPjName().split(",");
           // pjNameList = new ArrayList<>(Arrays.asList(pjNames));
        }
        if (StringUtils.isNotEmpty(rowResult.getPjLocation())){
            String[] pjLocations = rowResult.getPjLocation().split(",");
            //pjLocationList = new ArrayList<>(Arrays.asList(pjLocations));
        }*/
        //时间分区
        /*List<String> partitions = partitions(rowResult.getSearchTime());*/
        List<String> partitions = partitions("three");
        //字段类型分区
        String typePartition = fieldType2Partition(rowResult.getPartition());
        Map paramMap = new HashMap();
        StringBuilder comSql = new StringBuilder();
        paramMap.put("partitions", partitions);
        paramMap.put("tableName", rowResult.getTableName());
        paramMap.put("pjname", rowResult.getPjName());
        paramMap.put("pjlocation", rowResult.getPjLocation());
        paramMap.put("rowName", rowResult.getRowName());
        paramMap.put("typePartition", typePartition);
        comSql.append("with a as (select count(*) as cc from sql_tmp where   partition in (:partitions) and tag='fp_table' and table_name =:tableName  and  pjname =:pjname and  pjlocation =:pjlocation limit 1), " +
                " b as (select row_name,count(*) as row_count from sql_tmp where  partition in (:partitions) and tag=:typePartition and  pjname =:pjname and  pjlocation =:pjlocation and table_name =:tableName  group by row_name ) " +
                " select num,row_name, row_count,percent from ( select row_number() OVER (order by row_count desc)  num,row_name, row_count,concat (round(b.row_count/a.cc*100,3),'%') as percent from b,a)t  where ");
        if (StringUtils.isNotBlank(rowResult.getRowName())) {
            comSql.append("  row_name=:rowName  and ");
        }
        //统计总条数
        StringBuilder countSql = new StringBuilder();
        countSql.append("with a as (select count(*) as cc from sql_tmp where   partition in (:partitions) and tag='fp_table' and table_name =:tableName  and  pjname =:pjname and  pjlocation =:pjlocation limit 1), " +
                " b as (select row_name,count(*) as row_count from sql_tmp where  partition in (:partitions) and tag=:typePartition and  pjname =:pjname and  pjlocation =:pjlocation and table_name =:tableName  group by row_name ) " +
                " select count(*) as count from ( select row_number() OVER (order by row_count desc)  num,row_name, row_count,concat (round(b.row_count/a.cc*100,3),'%') as percent from b,a)t  ");
        if (StringUtils.isNotBlank(rowResult.getRowName())) {
            countSql.append(" where  row_name=:rowName ");
        }

        if (page != null) {
            int total = 0;
            List<RowResult> totalList = namedParameterJdbcTemplate.query(countSql.toString(), paramMap, new BeanPropertyRowMapper<>(RowResult.class));
            if (totalList != null && totalList.size() > 0 && totalList.get(0).getCount() != null) {
                total = totalList.get(0).getCount();
            }
            page.setTotalRows(total);
            comSql.append("   num >" + page.getRowStart() + " and num <" + (page.getRowStart() + page.getPageSize()) + " limit " + page.getPageSize() + " ");
        }
        List<RowResult> list = namedParameterJdbcTemplate.query(comSql.toString(), paramMap, new BeanPropertyRowMapper<>(RowResult.class));
        return list;
    }

    @Override
    public List<AllResult> tagList() {
        String sql = " select tag from all_result where partition like '%' group by tag ";
        List<AllResult> qualifiedCountList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AllResult.class));
        return qualifiedCountList;
    }


    private void jointSql(StringBuilder sql, String tag, int i) {
        if (i == 0) {
            if (StringUtils.equals("1", tag)) {
                sql.append("  (time < 10000) ");
            }
            if (StringUtils.equals("2", tag)) {
                sql.append("  (time < 20000) ");
            }
            if (StringUtils.equals("3", tag)) {
                sql.append(" (time > 20000 and time < 60000) ");
            }
            if (StringUtils.equals("4", tag)) {
                sql.append(" (time > 60000) ");
            }
        } else {
            if (StringUtils.equals("1", tag)) {
                sql.append(" or (time < 10000) ");
            }
            if (StringUtils.equals("2", tag)) {
                sql.append(" or (time < 20000) ");
            }
            if (StringUtils.equals("3", tag)) {
                sql.append(" or (time > 20000 and time < 60000) ");
            }
            if (StringUtils.equals("4", tag)) {
                sql.append(" or (time > 60000) ");
            }
        }

    }

    private String fieldType2Partition(String type) {
        String partition = "";
        switch (type) {
            case "1":
                partition = "fp_field";
                break;
            case "2":
                partition = "fp_equal";
                break;
            case "3":
                partition = "fp_size";
                break;
            case "4":
                partition = "fp_order";
                break;
            case "5":
                partition = "fp_group";
                break;
            case "6":
                partition = "fp_like";
                break;
            default:
        }
        return partition;
    }

    private static final int MONTH_3 = 3;
    private static final int MONTH_6 = 6;
    private static final int MONTH_12 = 12;

    public static List<String> partitions(String partition) {
        List<String> partitions = null;
        switch (partition) {
            case "one":
                partitions = TimeUtil.partitions(1);
                break;
            case "three":
                partitions = TimeUtil.partitions(MONTH_3);
                break;
            case "half":
                partitions = TimeUtil.partitions(MONTH_6);
                break;
            case "year":
                partitions = TimeUtil.partitions(MONTH_12);
                break;
            default:
        }
        return partitions;
    }

    private void concatSql(RowResult rowResult, StringBuilder comSql, Map paramMap, List<String> pjNameList, List<String> pjLocationList, List<String> partitions) {
        if (StringUtils.isNotBlank(rowResult.getTableName())) {
            comSql.append(" and table_name =:tableName ");
            paramMap.put("tableName", rowResult.getTableName());
        }
        if (StringUtils.isNotBlank(rowResult.getPartition())) {
            comSql.append(" and row_partition in( :partitions ) ");
            paramMap.put("partitions", partitions);
        }

        if (StringUtils.isNotBlank(rowResult.getPjName())) {
            comSql.append(" and  pjname in(:pjNameList) ");
            paramMap.put("pjNameList", pjNameList);
        }

        if (StringUtils.isNotBlank(rowResult.getPjLocation())) {
            comSql.append(" and  pjlocation in(:pjLocationList) ");
            paramMap.put("pjLocationList", pjLocationList);
        }
    }


    private void joinSql(List<String> pjNames, List<String> pjLocations, Map<String, Object> param, StringBuilder sql) {
        if (pjNames != null && pjNames.size() > 0 && !pjNames.contains("all")) {
            sql.append(" and  pjname in (:pjName)");
            param.put("pjName", pjNames);
        }
        if (pjLocations != null && pjLocations.size() > 0 && !pjLocations.contains("all")) {
            sql.append(" and  pjlocation in (:pjLocation)");
            param.put("pjLocation", pjLocations);
        }
    }


}
