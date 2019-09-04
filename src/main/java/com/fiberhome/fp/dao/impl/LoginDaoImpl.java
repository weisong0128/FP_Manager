package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.LoginDao;
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
    public int login(String userName, String passWord) {
        StringBuilder sql = new StringBuilder("select count(uuid) as count from fp_user where user_state !='2' and user_name=? and user_password=?");
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString(), new Object[]{userName,passWord});
        return Integer.valueOf(maps.get(0).get("count").toString());
    }
}
