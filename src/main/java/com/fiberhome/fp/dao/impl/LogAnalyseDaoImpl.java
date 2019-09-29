package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.LogAnalzeDao;
import com.fiberhome.fp.pojo.AllResult;
import com.fiberhome.fp.pojo.ErrorResult;
import com.fiberhome.fp.pojo.FpOperationTable;
import com.fiberhome.fp.pojo.LogAnalze;
import com.fiberhome.fp.util.EntityMapTransUtils;
import com.fiberhome.fp.util.Page;
import com.fiberhome.fp.util.TimeUtil;
import com.fiberhome.fp.vo.ErrorSqlCount;
import com.fiberhome.fp.vo.SqlCount;
import com.fiberhome.fp.vo.TagProporation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    static Logger logging = LoggerFactory.getLogger(LogAnalyseDaoImpl.class);

    @Autowired
    @Qualifier("hiveNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public static final String TODAY = "today";
    public static final String SEVEN = "seven";
    public static final String HALFMONTH = "halfMonth";
    public static final String ALL = "all";
    public static final int SEVENNUM = 7;
    public static final int HALFMONTHNUM = 15;


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
            logging.error(e.getMessage(), e);
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
            logging.error(e.getMessage(), e);
        }
        return update;
    }

    public int updateLogAnalze(LogAnalze logAnalze) {
        String sql = "UPDATE fp_log_analyze SET project_name=? ,address=? , parsing_state=?,progress=?,result=?,create_time=?,update_time=? WHERE uuid=?";
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
            logging.error(e.getMessage(), e);
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
            logging.error(e.getMessage(), e);
        }
        return update;
    }

    @Override
    public LogAnalze findOneLogAnalyse(String uuid) {
        String sql = "SELECT " + LogAnalze.getAllColumn() + "from fp_log_analyze where uuid= ?";
        LogAnalze logAnalze = null;
        try {
            List<LogAnalze> query = mysqlJdbcTemplate.query(sql, new String[]{uuid}, new BeanPropertyRowMapper<>(LogAnalze.class));
            if (query != null && !query.isEmpty()) {
                logAnalze = query.get(0);
            }
        } catch (DataAccessException e) {
            logging.error(e.getMessage(), e);
        }
        return logAnalze;
    }

    /**
     * @description:根据uuid列表返回list
     * @Param:[uuids]
     * @Return:java.util.List<com.fiberhome.fp.pojo.LogAnalze>
     * @Auth:User on 2019/9/29 9:12
     */
    public List<LogAnalze> findLogAnalyseListByUuids(List<String> uuids) {

        StringBuilder sql = new StringBuilder("SELECT " + LogAnalze.getAllColumn() + "from fp_log_analyze where 1 = ?");
        ArrayList<Object> list = new ArrayList<>();
        list.add("1");
        if (uuids != null) {
            getInSqlTemplate(sql, list, uuids, "uuid");
        }
        List<LogAnalze> query = mysqlJdbcTemplate.query(sql.toString(), list.toArray(), new BeanPropertyRowMapper<>(LogAnalze.class));
        return query;
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
            if (StringUtils.equals(TODAY, param.getTimeTag())) {
                param.setStarTime(TimeUtil.beforeFewDays(0) + "");
            } else if (StringUtils.equals(SEVEN, param.getTimeTag())) {
                param.setStarTime(TimeUtil.beforeFewDays(SEVENNUM) + "");
            } else if (StringUtils.equals(HALFMONTH, param.getTimeTag())) {
                param.setStarTime(TimeUtil.beforeFewDays(HALFMONTHNUM) + "");
            } else if (StringUtils.equals(ALL, param.getTimeTag())) {
                param.setStarTime(null);
                param.setEndTime(null);
            }
        }
        List<String> projectNameList = param.getProjectNameList();
        if (projectNameList != null && !projectNameList.isEmpty()) {
            getInSqlTemplate(sql, list, projectNameList, "project_name");
        }
        List<String> addressList = param.getAddressList();
        if (addressList != null && !addressList.isEmpty()) {
            getInSqlTemplate(sql, list, addressList, "address");
        }
        if (param.getStarTime() != null && !param.getStarTime().equalsIgnoreCase("")) {
            sql.append("and create_time > ? ");
            list.add(param.getStarTime());
        }
        String userId = param.getUserId();
        if (!"1".equals(userId)) {
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
            logging.error(e.getMessage(), e);
            throw e;
        }
        return logAnalzeList;
    }

    /**
     * sql错误页面
     */
    @Override
    public List<ErrorResult> listErrResult(Page page, ErrorResult errorResult) {
        StringBuilder sql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        //是否去重
        boolean isDistinct = errorResult.getIsDistinct();
        String date = isDistinct ? "max(date) as date" : " date ";
        sql.append(" select  " + date + ",tag,alter_tag,sql_result,pjname,pjlocation from err_result  WHERE   syskv='nothing:1' ");
        countSql.append("select  count(*) as count  from err_result  WHERE   syskv='nothing:1' ");
        Map paramMap = new HashMap();
        //根据时间过滤
        if (isDistinct) {
            sql.append(" and partition in (:partition) ");
            countSql.append(" and partition in (:partition) ");
            paramMap.put("partition", TimeUtil.partitons(SEVEN));
        } else if (StringUtils.isNotEmpty(errorResult.getTimeTag())) {
            if (StringUtils.equals(TODAY, errorResult.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons(TODAY));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(0));
            } else if (StringUtils.equals(SEVEN, errorResult.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons(SEVEN));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(SEVENNUM));
            } else if (StringUtils.equals(HALFMONTH, errorResult.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.partitons(HALFMONTH));
                sql.append(" and date > :date ");
                countSql.append(" and date > :date ");
                paramMap.put("date", TimeUtil.beforeFewDays(HALFMONTHNUM));
            } else if (StringUtils.equals("customZone", errorResult.getTimeTag())) {
                sql.append(" and partition in (:partition) ");
                countSql.append(" and partition in (:partition) ");
                paramMap.put("partition", TimeUtil.long2String(errorResult.getCaptureTime(), "yyyyMM"));
                sql.append(" and date > :startTime ");
                countSql.append(" and date > :startTime ");
                paramMap.put("startTime", errorResult.getStartTime());
                sql.append(" and date < :endTime ");
                countSql.append(" and date < :endTime ");
                paramMap.put("endTime", errorResult.getEndTime());
            } else if (StringUtils.equals(ALL, errorResult.getTimeTag())) {
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
        if (errorResult.getPjNameList() != null && !errorResult.getPjNameList().isEmpty() && !errorResult.getPjNameList().contains(ALL)) {
            sql.append(" and  pjname in (:pjName)");
            countSql.append(" and  pjname in (:pjName)");
            paramMap.put("pjName", errorResult.getPjNameList());
        }
        //根据项目地点过滤
        if (errorResult.getPjLocationList() != null && !errorResult.getPjLocationList().isEmpty() && !errorResult.getPjLocationList().contains(ALL)) {
            sql.append(" and  pjlocation in (:pjLocation) ");
            countSql.append(" and  pjlocation in (:pjLocation) ");
            paramMap.put("pjLocation", errorResult.getPjLocationList());
        }

        //根据错误类型过滤
        if (errorResult.getTagList() != null && !errorResult.getTagList().isEmpty() && !errorResult.getTagList().contains(ALL)) {
            getOrSqlTemplate(sql, countSql, paramMap, "errorResult", errorResult.getTagList());
        }
        if (StringUtils.isNotBlank(errorResult.getErrorSqlType())) {
            //此处替换前端发送的3000  数据库存储的3W
            errorResult.setErrorSqlType(errorResult.getErrorSqlType().replace("limit超过30000", "3W"));
            List<String> errorSqlType = EntityMapTransUtils.StringToList(errorResult.getErrorSqlType());
            if (errorSqlType != null && !errorSqlType.isEmpty() && !errorSqlType.contains(ALL)) {
                getOrSqlTemplate(sql, countSql, paramMap, "errorSqlType", errorSqlType);
            }
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
                logging.error(e.getMessage(), e);
                throw e;
            }
            if (count != null && !count.isEmpty() && count.get(0).getCount() != null) {
                total = count.get(0).getCount();
            }
            page.setTotalRows(total);
            String sort = errorResult.getSort() != null ? errorResult.getSort() : "DESC";
            sql.append(" ORDER BY date " + sort + " limit " + page.getRowStart() + "," + page.getPageSize());

        }
        List query = null;
        try {
            query = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(ErrorResult.class));
        } catch (DataAccessException e) {
            logging.error(e.getMessage(), e);
            throw e;
        }
        return query;
    }


    //参数  sql  , paramMap , 字段 , 需要传入的list
    public void getOrSqlTemplate(StringBuilder sql, StringBuilder countSql, Map paramMap, String colums, List<String> list) {
        sql.append(" and (");
        countSql.append(" and (");
        for (int i = 0; i < list.size(); i++) {
            String s = i == list.size() - 1 ? "SEARCH_ALL like  :" + colums + "" + i : "SEARCH_ALL like  :" + colums + "" + i + " or ";
            sql.append(s);
            countSql.append(s);
            String s1 = list.get(i);
            paramMap.put(colums + i, s1);
        }
        sql.append(" )");
        countSql.append(" )");

    }


    public void getInSqlTemplate(StringBuilder sql, List list, List paramList, String colums) {
        sql.append("and " + colums + " in ( ");
        for (int i = 0; i < paramList.size(); i++) {
            sql.append(i == paramList.size() - 1 ? " ? " : " ? ,");
            list.add(paramList.get(i));
        }
        sql.append(" ) ");
    }

    @Override
    public List<ErrorResult> wordExportErrorResult(String pjName, String pjLocation, String createTime) {
        String partition = TimeUtil.long2String(Long.parseLong(createTime), "yyyyMM");
        String sql = "select count(*) as count,tag,alter_tag from err_result " +
                "where partition like " + partition + " and pjname =:pjName and pjlocation = :pjLocation and capture_time = :captureTime " +
                " group by tag,alter_tag";
        Map paramMap = new HashMap();
        // paramMap.put("partition", partition);
        paramMap.put("pjName", pjName);
        paramMap.put("pjLocation", pjLocation);
        paramMap.put("captureTime", createTime);
        List<ErrorResult> query = namedParameterJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<>(ErrorResult.class));
        for (ErrorResult errorResult : query) {
            String newSql = "select sql_result from err_result where partition like '" + partition + "' and pjname= '" + pjName + "' and pjlocation = '" + pjLocation + "' and capture_time = " + createTime + " " +
                    "and tag = '" + errorResult.getTag() + "' limit 1";
            String s = namedParameterJdbcTemplate.queryForObject(newSql, new HashMap<>(), String.class);
            errorResult.setSqlResult(s);
        }
        return query;
    }


    public List<FpOperationTable> wordExportFpOperationTable(String pjName, String pjLocation, String createTime) {
        String partition = TimeUtil.long2String(Long.parseLong(createTime), "yyyyMM");
        String sql = "select min(date) as date,count(*) as count ,errcode,errinfo from fp_operation_table where partition like '" + partition + "' " +
                "and pjname='" + pjName + "' and pjlocation='" + pjLocation + "' and capture_time = '" + createTime + "' group by errcode,errinfo;";
       /* Map paramMap = new HashMap();
        // paramMap.put("partition", partition);
        paramMap.put("pjName", pjName);
        paramMap.put("pjLocation", pjLocation);
        paramMap.put("captureTime", createTime);*/
        List<FpOperationTable> query = namedParameterJdbcTemplate.query(sql, new HashMap<>(), new BeanPropertyRowMapper<>(FpOperationTable.class));
        return query;
    }

    public AllResult getProportion(String pjName, String pjLocation, String createTime) {
        String partition = TimeUtil.long2String(Long.parseLong(createTime), "yyyyMM");
        AllResult allResult = new AllResult();
        Map<String, Object> param = new HashMap();
        StringBuilder qualifiedSql = new StringBuilder();
        qualifiedSql.append(" select count(*) as count from all_result where partition like " + partition);
        qualifiedSql.append(" and pjname =:pjName and pjlocation = :pjLocation and capture_time = :captureTime ");
        StringBuilder unqualifiedSql = new StringBuilder();
        unqualifiedSql.append(" select count(*) as count from err_result where partition  like  " + partition);
        unqualifiedSql.append(" and pjname =:pjName and pjlocation = :pjLocation and capture_time = :captureTime ");
        param.put("pjName", pjName);
        param.put("pjLocation", pjLocation);
        param.put("captureTime", createTime);

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
        allResult.setQualifiedSql(qualifiedSqlCount);
        allResult.setUnqualifiedSql(unqualifiedSqlCount);
        return allResult;

    }


}
