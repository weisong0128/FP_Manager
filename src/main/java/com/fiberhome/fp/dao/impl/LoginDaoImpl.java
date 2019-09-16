package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.LoginDao;
import com.fiberhome.fp.pojo.UserInfo;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Repository
public class LoginDaoImpl implements LoginDao {

    @Resource(name = "mysqlJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Override
    public UserInfo login(String userName, String passWord) {
        StringBuilder sql = new StringBuilder("select * from fp_user where user_state !='2' and user_name=? and user_password=?");
        List<UserInfo>  userInfos = jdbcTemplate.query(sql.toString(), new Object[]{userName,passWord},new BeanPropertyRowMapper<>(UserInfo.class));
        UserInfo userInfo=null;
        if (userInfos.size()>0){
            userInfo=userInfos.get(0);
        }
        return userInfo;
    }
}
