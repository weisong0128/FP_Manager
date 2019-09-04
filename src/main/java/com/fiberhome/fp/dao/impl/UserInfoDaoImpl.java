package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.UserInfoDao;
import com.fiberhome.fp.pojo.ErrorResult;
import com.fiberhome.fp.pojo.UserInfo;
import com.fiberhome.fp.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

@Repository
public class UserInfoDaoImpl implements UserInfoDao {

    @Resource(name = "mysqlJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("mysqlNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 创建用户方法
     *
     * @param user 用户实体
     * @return
     */
    @Override
    public String createUser(UserInfo user) {
        user.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        StringBuilder sql = new StringBuilder("INSERT INTO fp_user ( uuid, user_id, user_name, user_password, user_role, user_state,create_time ) VALUES (?,?,?,?,?,?,?)");
        int count = jdbcTemplate.update(sql.toString(), new Object[]{user.getUuid(), user.getUserId(), user.getUserName(), user.getUserPassword(), user.getUserRole(), user.getUserState(), new Date()});
        if (count >0) {
            return user.getUuid();
        }else {
            return "0";
        }
    }

    //修改
    @Override
    public int updateUserInfo(UserInfo user) {
        StringBuilder sql=null;
        Map<String,Object> parames =  new HashMap<>();
        sql= new StringBuilder("update  fp_user set user_id=:userId,user_name=:userName,user_password=:passWord,user_role=:role,user_state=:state,update_time=:updateTime   where uuid = :uuid");
        parames.put("uuid", user.getUuid());
        parames.put("userId", user.getUserId());
        parames.put("userName", user.getUserName());
        parames.put("passWord", user.getUserPassword());
        parames.put("role", user.getUserRole());
        parames.put("state", user.getUserState());
        parames.put("updateTime", new Date());
        return namedParameterJdbcTemplate.update(sql.toString(), parames);
    }

    @Override
    public UserInfo getUserInfoByUuid(String uuid) {
        String sql="select * from fp_user where uuid=?  and user_state !='2'";
        List<UserInfo>  userInfos = jdbcTemplate.query(sql.toString(),new Object[]{uuid},new BeanPropertyRowMapper<>(UserInfo.class));
        return userInfos.get(0);
    }

    /**
     * 2-删除，0-启用，1-停用
     * @param state
     * @return
     */
    @Override
    public int updateState(String state, Map<String,Object> parames) {
        parames.put("updateTime",new Date());
        StringBuilder sql = new StringBuilder();
        if ("0".equals(state)){
            sql.append("update  fp_user set user_state='0' , update_time=:updateTime where uuid in (:uuids)");
        }else  if("1".equals(state)){
            sql.append("update  fp_user set user_state='1', update_time=:updateTime  where uuid in (:uuids)");
        }else if ("2".equals(state)){
            sql.append("update  fp_user set user_state='2', update_time=:updateTime  where uuid in (:uuids)");
        }
        return namedParameterJdbcTemplate.update(sql.toString(),parames);
    }

    /**
     * 获取所有的用户信息
     */
    @Override
    public List<UserInfo> getAllUserInfo(Page page,UserInfo userInfo) {
        StringBuilder sql = new StringBuilder("select uuid,user_id,user_name,user_password,user_role,user_state,create_time,update_time from fp_user where user_state !='2' ");
        Map<String, Object> map = concatSql(sql, userInfo);
        sql =(StringBuilder)map.get("sql");
        Map<String,Object> parames = (Map<String,Object>)map.get("parames");
        StringBuilder  userCount =  new StringBuilder(" SELECT count(*) as totalrows FROM  fp_user WHERE user_state !='2'  ");
        if (page != null){
            int total  = 0;
            Map<String, Object> map1 =concatSql(userCount,userInfo);
            userCount =(StringBuilder)map1.get("sql");
            List<Map<String, Object>> count = namedParameterJdbcTemplate.queryForList(userCount.toString(), parames);
            if(count.get(0).get("totalrows")!=null){
                total= Integer.valueOf(count.get(0).get("totalrows").toString());
            }
            page.setTotalRows(total);
        }
        sql.append("  limit  " +page.getRowStart()+","+page.getPageSize());
        return namedParameterJdbcTemplate.query(sql.toString(),parames,new BeanPropertyRowMapper<UserInfo>(UserInfo.class));
    }
    /**
     * 封装条件查询的方法
     * 参数：sql
     */
    private  Map<String,Object> concatSql(StringBuilder sql,UserInfo userInfo){
        Map<String,Object> resultMap  = new HashMap<>();
        Map<String,Object> parames = new HashMap<>();
        if(userInfo!=null){
         /*   if (userInfo.getUserId()!=null){
                sql.append(" AND user_id LIKE  concat('%',:userId,'%' )");
                parames.put("userId",userInfo.getUserId());
            }*/
            if (userInfo.getUserName()!=null){
                sql.append(" and  (user_id like concat('%',:keyword,'%') or user_name like concat('%',:keyword,'%')) ");
                parames.put("keyword",userInfo.getUserName());
            }
            if (userInfo.getUserRole()!=null){
                sql.append(" AND  user_role LIKE concat( '%',:userRole,'%' )");
                parames.put("userRole",userInfo.getUserRole());
            }
            if (userInfo.getUserState()!=null){
                sql.append(" AND  user_state LIKE concat( '%',:userState,'%' )");
                parames.put("userState",userInfo.getUserState());
            }
        }
        resultMap.put("sql",sql);
        resultMap.put("parames",parames);
        return resultMap;
    }
}
