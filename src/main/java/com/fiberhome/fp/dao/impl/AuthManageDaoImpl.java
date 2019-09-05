package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.AuthManageDao;
import com.fiberhome.fp.pojo.AuthManage;
import com.fiberhome.fp.util.EntityMapTransUtils;
import com.fiberhome.fp.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class AuthManageDaoImpl implements AuthManageDao {
    @Resource(name = "mysqlJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("mysqlNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     新增授权
     @param authManage
     @return
     */
    @Override
    public String createAuthManage(AuthManage authManage) {
        StringBuilder sql = new StringBuilder("INSERT INTO `fp_myql_test`.`fp_auth_management`(`uuid`, `project_name`, `envir_head`, `phone`, `provinces`, `cities`, `address`, `mac`," +
                " `master_ip`, `download_time`, `envir_note`, `sn_file`, `feedback`, `note`, `create_time`, `update_time`) VALUES (:uuid, :projectName, :envirHead, :phone, :provinces, :cities, :address, :mac," +
                " :masterIp, :downloadTime, :envirNote, :snFile, :feedback, :note, :createTime, :updateTime)");
        authManage.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        authManage.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        authManage.setUpdateTime(null);
        int count = namedParameterJdbcTemplate.update(sql.toString(), EntityMapTransUtils.entityToMap1(authManage));
        if (count>0){
            return authManage.getUuid();
        }else {
            return "0";
        }

    }

    @Override
    public List<AuthManage> getAllAuthManage(Page page, AuthManage authManage) {
        StringBuilder sql = new StringBuilder("SELECT * FROM fp_auth_management where 1=1  and is_available != '1' ");
        Map<String,Object> parames = new HashMap<>();
        Map<String, Object> map = concatSql(sql, authManage);
        sql = (StringBuilder) map.get("sql");
        parames= ( Map<String,Object>)map.get("parames");
        //分页
        StringBuilder countsql=new StringBuilder("SELECT count(*) as totalrows FROM fp_auth_management where 1=1  and is_available != '1' ");
        if(page!=null){
            map = concatSql(countsql, authManage);
            countsql = (StringBuilder) map.get("sql");
            List<Map<String, Object>> count = namedParameterJdbcTemplate.queryForList(countsql.toString(), parames);
            if(count.get(0).get("totalrows")!=null){
                int total= Integer.valueOf(count.get(0).get("totalrows").toString());
                page.setTotalRows(total);
            }
        }
        sql.append("  limit "+page.getRowStart()+","+page.getPageSize());
        return namedParameterJdbcTemplate.query(sql.toString(),parames,new BeanPropertyRowMapper<>(AuthManage.class));
    }
    @Override
    public int updateAndDelete(Map<String, Object> parames) {
        StringBuilder sql = null;
        if (parames.get("uuids")!=null){
            sql = new StringBuilder("UPDATE fp_auth_management SET is_available = '1' where uuid in (:uuids)");
        }else{
            parames.put("updateTime",new Date());
            sql = new StringBuilder("UPDATE fp_auth_management " +
                    "SET  `project_name` = :projectName ," +
                    " `envir_head` = :envirHead ," +
                    " `phone` = :phone ," +
                    " `provinces` = :provinces ," +
                    " `cities` = :cities ," +
                    " `address` = :address ," +
                    " `mac` = :mac ," +
                    " `master_ip` =:masterIp ," +
                    " `download_time` =:downloadTime ," +
                    " `envir_note` = :envirNote ," +
                    " `sn_file` = :snFile ," +
                    " `feedback` = :feedback ," +
                    " `note` = :note ," +
                    " `update_time` = :updateTime " +
                    " WHERE" +
                    " uuid = :uuid ");
        }
        return namedParameterJdbcTemplate.update(sql.toString(),parames);
    }

    @Override
    public List<Map<String, Object>> getAllCities() {
        StringBuilder sqlPjname = new StringBuilder("SELECT DISTINCT  cities  FROM  fp_auth_management WHERE is_available !='1' ");
        return namedParameterJdbcTemplate.queryForList(sqlPjname.toString(),new HashMap<>());
    }

    @Override
    public List<Map<String, Object>> getAllPjName() {
        StringBuilder sqlCities = new StringBuilder("SELECT DISTINCT project_name  FROM  fp_auth_management WHERE is_available !='1' ");
        return namedParameterJdbcTemplate.queryForList(sqlCities.toString(),new HashMap<>());
    }

    /**
     * 封装条件查询的方法
     * 参数：sql
     */
    private  Map<String,Object> concatSql(StringBuilder sql,AuthManage authManage){
        Map<String,Object> resultMap  = new HashMap<>();
        Map<String,Object> parames = new HashMap<>();
        if (authManage!=null){
            if (authManage.getProjectName()!=null && authManage.getProjectName()!=""){
                sql.append(" and project_name in (:projectNames) ");
                List<String> projectNames = EntityMapTransUtils.StringToList(authManage.getProjectName());
                parames.put("projectNames",projectNames);
            }
            if(authManage.getEnvirNote()!=null&& authManage.getEnvirNote()!=""){
                sql.append(" and envir_note =:envirNote ");
                parames.put("envirNote",authManage.getEnvirNote());
            }
            if (authManage.getCities()!=null&& authManage.getCities()!=""){
                sql.append(" and cities in (:cities) ");
                List<String> cities = EntityMapTransUtils.StringToList(authManage.getCities());
                parames.put("cities",cities);
            }
            if (authManage.getFeedback()!=null&& authManage.getFeedback()!=""){
                sql.append(" and   feedback =:feedback ");
                parames.put("feedback",authManage.getFeedback());
            }

            if (authManage.getStartTime()!=null&&authManage.getEndTime()!=null&& authManage.getStartTime()!=""&&authManage.getEndTime()!=""){
                sql.append(" and   download_time between :startTime  and  :endTime  ");
                parames.put("startTime",authManage.getStartTime());
                parames.put("endTime",authManage.getEndTime());
            }
            if (authManage.getKeyWord()!=null&& authManage.getKeyWord()!=""){
                sql.append(" and(project_name like concat('%',:keyWord,'%') or envir_head like concat('%',:keyWord,'%') or phone like concat('%',:keyWord,'%') or provinces like concat('%',:keyWord,'%') or cities like concat('%',:keyWord,'%') or master_ip like concat('%',:keyWord,'%') )  ");
                parames.put("keyWord",authManage.getKeyWord());
            }
            if (authManage.getSortField()!=null&& authManage.getSortField()!=""){
                if ("desc".equals(authManage.getSortField())){
                    sql.append("  ORDER BY download_time DESC  ");
                }else if("asc".equals(authManage.getSortField())){
                    sql.append("  ORDER BY download_time asc  ");
                }
            }
        }
        resultMap.put("sql",sql);
        resultMap.put("parames",parames);
        return resultMap;
    }
}
