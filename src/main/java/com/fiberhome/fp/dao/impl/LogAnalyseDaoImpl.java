package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.LogAnalzeDao;
import com.fiberhome.fp.pojo.ErrorResult;
import com.fiberhome.fp.pojo.FpOperationTable;
import com.fiberhome.fp.pojo.LogAnalze;
import com.fiberhome.fp.util.Page;
import com.fiberhome.fp.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

@Repository
public class LogAnalyseDaoImpl implements LogAnalzeDao {

    @Resource(name = "hiveJdbcTemplate")
    JdbcTemplate hiveJdbcTemplate;

    @Autowired
    @Qualifier("mysqlJdbcTemplate")
    JdbcTemplate mysqlJdbcTemplate;


    @Autowired
    @Qualifier("hiveNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public boolean createLogAnalze(LogAnalze logAnalze) {
        String sql = "INSERT INTO fp_log_analyze VALUES (?,?,?,?,?,?,?,?,?,?)";
        ArrayList<Object> param = new ArrayList<>();
        param.add(logAnalze.getUuid());
        param.add(logAnalze.getProjectName());
        param.add(logAnalze.getAddress());
        param.add(new Date());
        param.add("3");
        param.add(0);
        param.add("");
        param.add(logAnalze.getCreateTime());
        param.add(new Date());
        param.add(logAnalze.getUserId());
        try {
            mysqlJdbcTemplate.update(sql, param.toArray());
            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int updateLogAnalze(Map<String, String> param, String uuid) {
        StringBuilder builder = new StringBuilder("UPDATE fp_log_analyze SET");
        Set<Map.Entry<String, String>> entries = param.entrySet();
        ArrayList<String> list = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.append(" ? = ? ");
            list.add(key);
            list.add(value);
            i++;
            if (i < entries.size()) {
                builder.append(",");
            }
        }
        builder.append("WHERE uuid = ?");
        list.add(uuid);
        int update = 0;
        try {
            update = mysqlJdbcTemplate.update(builder.toString(), list.toArray());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return update;
    }

    public int updateLogAnalze(LogAnalze logAnalze) {
        String sql = ("UPDATE fp_log_analyze SET project_name=? ,address=? , parsing_state=?,progress=?,result=?,create_time=?,update_time=? WHERE uuid=?");
        ArrayList<Object> list = new ArrayList<>();
        list.add(logAnalze.getProjectName());
        list.add(logAnalze.getAddress());
        list.add(logAnalze.getParsingState());
        list.add(logAnalze.getProgress());
        list.add(logAnalze.getResult());
        list.add(logAnalze.getCreateTime());
        list.add(new Date());
        list.add(logAnalze.getUuid());
        int update = 0;
        try {
            update = mysqlJdbcTemplate.update(sql, list.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }


    @Override
    public int deleteLogAnalze(String uuid) {
        String sql = "DELETE FROM fp_log_analyze WHERE uuid = ?";
        int update = 0;
        try {
            update = mysqlJdbcTemplate.update(sql, uuid);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return update;
    }

    @Override
    public LogAnalze findOneLogAnalyse(String uuid) {
        String sql = "SELECT " + LogAnalze.getAllColumn() + "from fp_log_analyze where uuid= ?";
        LogAnalze logAnalze = null;
        try {
            List<LogAnalze> query = mysqlJdbcTemplate.query(sql, new String[]{uuid}, new BeanPropertyRowMapper<>(LogAnalze.class));
            if (query != null && query.isEmpty()) {
                logAnalze = query.get(0);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return logAnalze;
    }

    /**
     *
     */
    @Override
    public List<LogAnalze> findLogAnalyseList(LogAnalze param, Page page) {
        String querySql = "SELECT " + LogAnalze.getAllColumn() + " from fp_log_analyze where 1 = ? ";
        String countSql = "SELECT count(*) from fp_log_analyze where 1 = ?  ";
        StringBuilder sql = new StringBuilder();
        ArrayList<Object> list = new ArrayList<>();
        list.add(1);
        if (StringUtils.isNotEmpty(param.getTimeTag())) {
            if (StringUtils.equals("today", param.getTimeTag())) {
                param.setStarTime(TimeUtil.beforeFewDays(0) + "");
            } else if (StringUtils.equals("seven", param.getTimeTag())) {
                param.setStarTime(TimeUtil.beforeFewDays(7) + "");
            } else if (StringUtils.equals("halfMonth", param.getTimeTag())) {
                param.setStarTime(TimeUtil.beforeFewDays(15) + "");
            } else if (StringUtils.equals("all", param.getTimeTag())) {
                param.setStarTime(null);
                param.setEndTime(null);
            }
        }
        List<String> projectNameList = param.getProjectNameList();
        if (projectNameList != null && projectNameList.size() > 0) {
            sql.append("and project_name in ( ");
            for (int i = 0; i < projectNameList.size(); i++) {
                if (i == projectNameList.size() - 1) {
                    sql.append(" ? ");
                } else {
                    sql.append(" ? ,");
                }
                list.add(projectNameList.get(i));
            }
            sql.append(" ) ");
        }
        List<String> addressList = param.getAddressList();
        if (addressList != null && addressList.size() > 0) {
            sql.append("and address in ( ");
            for (int i = 0; i < addressList.size(); i++) {
                if (i == addressList.size() - 1) {
                    sql.append(" ? ");
                } else {
                    sql.append(" ? ,");
                }
                list.add(addressList.get(i));
            }
            sql.append(" ) ");
        }
        if (param.getStarTime() != null && !param.getStarTime().equalsIgnoreCase("")) {
            sql.append("and create_time > ? ");
            list.add(param.getStarTime());
        }
        String userId = param.getUserId();
        if (!"0".equals(userId)) {
            sql.append(" and user_id = ? ");
            list.add(userId);
        }
        if (param.getEndTime() != null && !param.getEndTime().equalsIgnoreCase("")) {
            sql.append("and create_time < ? ");
            list.add(param.getEndTime());
        }
        if (param.getParsingState() != null && param.getParsingState().equals("1")) {
            sql.append("and parsing_state !=  0 ");
        } else if (param.getParsingState() != null && param.getParsingState().equals("0")) {
            sql.append("and parsing_state != 1 ");
        }
        countSql += sql.toString();
        String sort = param.getSort();
        String ordetAndLimitSql = " ORDER BY start_time  " + sort + "  LIMIT ? , ?  ";
        querySql = querySql + sql.toString() + ordetAndLimitSql;
        int start = page.getRowStart();
        List<LogAnalze> logAnalzeList = null;
        try {
            Integer total = mysqlJdbcTemplate.queryForObject(countSql, Integer.class, list.toArray());
            list.add(start);
            list.add(page.getPageSize());
            logAnalzeList = mysqlJdbcTemplate.query(querySql, list.toArray(), new BeanPropertyRowMapper<>(LogAnalze.class));
            page.setTotalRows(total);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw e;
        }
        return logAnalzeList;
    }

    /**
     * sql错误页面
     */
    @Override
    public List<ErrorResult> ListErrResult(Page page, ErrorResult errorResult) {
        StringBuilder sql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        //是否去重
        boolean isDistinct = false;
        if (errorResult.getIsDistinct() != null && (errorResult.getIsDistinct() + "").equals("1")) {
            isDistinct = true;
        }
        String date = isDistinct ? "max(date) as date" : " date ";
        sql.append(" select  " + date + ",tag,alter_tag,sql_result,pjname,pjlocation from err_result  WHERE   syskv='nothing:1' ");
        countSql.append("select  count(*) as count  from err_result  WHERE   syskv='nothing:1' ");
        Map paramMap = new HashMap();
        //根据时间过滤
        if (isDistinct) {
            sql.append(" and partition in (:partition) ");
            countSql.append(" and partition in (:partition) ");
            paramMap.put("partition", TimeUtil.partitons("seven"));
        } else if (StringUtils.isNotEmpty(errorResult.getTimeTag())) {
            if (StringUtils.equals("today", errorResult.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons("today"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(0));
            } else if (StringUtils.equals("seven", errorResult.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons("seven"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(7));
            } else if (StringUtils.equals("halfMonth", errorResult.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons("halfMonth"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(15));
            } else if (StringUtils.equals("customZone", errorResult.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.long2String(Long.valueOf(errorResult.getCaptureTime()), "yyyyMM"));
                sql.append(" and date > :startTime ");
                countSql.append(" and date > :startTime ");
                paramMap.put("startTime", errorResult.getStartTime());
                sql.append(" and date < :endTime ");
                countSql.append(" and date < :endTime ");
                paramMap.put("endTime", errorResult.getEndTime());
            } else if (StringUtils.equals("all", errorResult.getTimeTag())) {
                sql.append(" and partition like '%' ");
                countSql.append(" and partition like '%' ");
            }
        } else {
            sql.append(" and partition like '%' ");
            countSql.append(" and partition like '%' ");
        }
        //根据分析时间过滤
        if (errorResult.getCaptureTime() != null) {
            sql.append(" and  capture_time = :captureTime");
            countSql.append(" and  capture_time = :captureTime");
            paramMap.put("captureTime", errorResult.getCaptureTime());
        }

        //根据项目名称过滤
        if (errorResult.getPjNameList() != null && errorResult.getPjNameList().size() > 0 && !errorResult.getPjNameList().contains("all")) {
            sql.append(" and  pjname in (:pjName)");
            countSql.append(" and  pjname in (:pjName)");
            paramMap.put("pjName", errorResult.getPjNameList());
        }
        //根据项目地点过滤
        if (errorResult.getPjLocationList() != null && errorResult.getPjLocationList().size() > 0 && !errorResult.getPjLocationList().contains("all")) {
            sql.append(" and  pjlocation in (:pjLocation) ");
            countSql.append(" and  pjlocation in (:pjLocation) ");
            paramMap.put("pjLocation", errorResult.getPjLocationList());
        }

        //根据错误类型过滤
        if (errorResult.getTagList() != null && errorResult.getTagList().size() > 0 && !errorResult.getTagList().contains("all")) {
            getOrSqlTemplate(sql, countSql, paramMap, "SEARCH_ALL", errorResult.getTagList());
        }
        //根据关键字过滤
        if (errorResult.getKeyWord() != null && !errorResult.getKeyWord().equalsIgnoreCase("")) {
            sql.append(" and SEARCH_ALL like :keyWord  ");
            countSql.append(" and SEARCH_ALL like :keyWord  ");
            paramMap.put("keyWord", errorResult.getKeyWord());
        }
        //去重拼接sql
        if (isDistinct) {
            String distinctSql = " group by tag,alter_tag,sql_result,pjname,pjlocation ";
            sql.append(distinctSql);
            countSql = new StringBuilder("select count(*) as count   from (" + sql.toString() + ")t");
        }
        if (page != null) {
            int total = 0;
            List<ErrorResult> count = null;

            try {
                count = namedParameterJdbcTemplate.query(countSql.toString(), paramMap, new BeanPropertyRowMapper<>(ErrorResult.class));
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            if (count != null && count.size() > 0 && count.get(0).getCount() != null) {
                total = count.get(0).getCount();
            }
            page.setTotalRows(total);
            sql.append(" ORDER BY date DESC limit " + page.getRowStart() + "," + page.getPageSize());

        }
        List query = null;
        try {
            query = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(ErrorResult.class));
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw e;
        }
        return query;
    }

    /**
     * 错误页面
     */
    @Override
    public List<FpOperationTable> list(Page page, FpOperationTable fpOperationTable) {

        StringBuilder sql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        boolean isDistinct = false;
        if (fpOperationTable.getIsDistinct() != null && (fpOperationTable.getIsDistinct() + "").equals("1")) {
            isDistinct = true;
        }
        String date = isDistinct ? "max(date) as date" : " date ";
        sql.append(" select  " + date + ",errcode,errinfo,pjname,pjlocation from fp_operation_table  WHERE   syskv='nothing:1' ");
        countSql.append("select  count(*) as count  from fp_operation_table  WHERE  syskv='nothing:1' ");
        Map paramMap = new HashMap();
        //时间条件
        if (isDistinct) {
            sql.append(" and partition in (:partition) ");
            countSql.append(" and partition in (:partition) ");
            paramMap.put("partition", TimeUtil.partitons("seven"));
          /*  sql.append(" and date > :date ");
            countSql.append(" and date > :date ");
            paramMap.put("date", TimeUtil.beforeFewDays(7));*/
        } else if (StringUtils.isNotEmpty(fpOperationTable.getTimeTag())) {
            if (StringUtils.equals("today", fpOperationTable.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons("today"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(0));
            }
            if (StringUtils.equals("seven", fpOperationTable.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons("seven"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(7));
            }
            if (StringUtils.equals("halfMonth", fpOperationTable.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons("halfMonth"));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(15));
            }
            if (StringUtils.equals("all", fpOperationTable.getTimeTag())) {
                sql.append(" and partition like '%' ");
                countSql.append(" and partition like '%' ");
            }
            if (StringUtils.equals("customZone", fpOperationTable.getTimeTag())) {
                long startTime = TimeUtil.beforeFewDays(30);
                long endTime = new Date().getTime();
                if (fpOperationTable.getStartTime() != null) {
                    startTime = Long.parseLong(fpOperationTable.getStartTime());
                }
                if (fpOperationTable.getEndTime() != null) {
                    endTime = Long.parseLong(fpOperationTable.getEndTime());
                }
                List<String> partitionList = TimeUtil.getMonthByLong(startTime, endTime);
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", partitionList);
                sql.append(" and date > :startTime ");
                countSql.append(" and date > :startTime ");
                paramMap.put("startTime", startTime);
                sql.append(" and date < :endTime ");
                countSql.append(" and date < :endTime ");
                paramMap.put("endTime", endTime);
            }
        } else {
            sql.append(" and partition in (:partition) ");
            countSql.append(" and partition in (:partition) ");
            paramMap.put("partition", TimeUtil.partitons("halfMonth"));
            sql.append(" and date > :date ");
            countSql.append(" and date > :date ");
            paramMap.put("date", TimeUtil.beforeFewDays(15));
        }
        //日志等级条件
        if (fpOperationTable.getLogLeaveList() != null && fpOperationTable.getLogLeaveList().size() > 0 && !fpOperationTable.getLogLeaveList().contains("all")) {
            List<String> logLeaveList = fpOperationTable.getLogLeaveList();
            sql.append(" and (");
            countSql.append(" and (");
            for (int i = 0; i < logLeaveList.size(); i++) {
                String s = i == logLeaveList.size() - 1 ? "errinfo like :errinfo" + "i " : "errinfo like :errinfo" + "i " + " or ";
                sql.append(s);
                countSql.append(s);
                paramMap.put("errinfo" + i, logLeaveList.get(i));
            }
            sql.append(" )");
            countSql.append(" )");
        }

        //根据关键字顾虑了
        if (fpOperationTable.getKeyWord() != null) {
            sql.append(" and errinfo like :keyWord ");
            countSql.append(" and errinfo like :keyWord ");
            paramMap.putIfAbsent("keyWord", fpOperationTable.getKeyWord());
        }

        //项目名称条件
        if (fpOperationTable.getPjNameList() != null && fpOperationTable.getPjNameList().size() > 0 && !fpOperationTable.getPjNameList().contains("all")) {
            sql.append(" and  pjname in (:pjName)");
            countSql.append(" and  pjname in (:pjName)");
            paramMap.put("pjName", fpOperationTable.getPjNameList());
        }
        //项目地市条件
        if (fpOperationTable.getPjLocationList() != null && fpOperationTable.getPjLocationList().size() > 0 && !fpOperationTable.getPjLocationList().contains("all")) {
            sql.append(" and  pjlocation in (:pjLocation)");
            countSql.append(" and  pjlocation in (:pjLocation)");
            paramMap.put("pjLocation", fpOperationTable.getPjLocationList());
        }
        //项目分析时间
        if (fpOperationTable.getCaptureTime() != null) {
            sql.append(" and  capture_time = :captureTime");
            countSql.append(" and  capture_time = :captureTime ");
            paramMap.put("captureTime", fpOperationTable.getCaptureTime());
        }

        //去重拼接sql
        if (isDistinct) {
            String distinctSql = " group by errcode,errinfo,pjname,pjlocation ";
            sql.append(distinctSql);
            countSql = new StringBuilder("select count(*) as count   from (" + sql.toString() + ")t");
        }
        if (page != null) {
            int total = 0;
            List<FpOperationTable> count = namedParameterJdbcTemplate.query(countSql.toString(), paramMap, new BeanPropertyRowMapper<>(FpOperationTable.class));
            if (count != null && count.size() > 0 && count.get(0).getCount() != null) {
                total = count.get(0).getCount();
            }
            page.setTotalRows(total);

            sql.append(" ORDER BY date DESC limit " + page.getRowStart() + "," + page.getPageSize());

        }

        return namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(FpOperationTable.class));

    }

    //参数  sql  , paramMap , 字段 , 需要传入的list
    public void getOrSqlTemplate(StringBuilder sql, StringBuilder countSql, Map paramMap, String colums, List<String> list) {
        sql.append(" and (");
        countSql.append(" and (");
        for (int i = 0; i < list.size(); i++) {
            String s = i == list.size() - 1 ? "" + colums + " like  :" + colums + "" + i : "" + colums + " like  :" + colums + "" + i + " or ";
            sql.append(s);
            countSql.append(s);
            String s1 = list.get(i);
            s1 = s1.replace(" ", "");
            paramMap.put(colums + i, s1);
        }
        sql.append(" )");
        countSql.append(" )");

    }
}
