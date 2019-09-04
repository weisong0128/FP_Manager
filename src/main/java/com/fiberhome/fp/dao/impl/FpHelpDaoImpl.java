package com.fiberhome.fp.dao.impl;

import com.fiberhome.fp.dao.FpHelpDao;
import com.fiberhome.fp.pojo.FpHelp;
import com.fiberhome.fp.util.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */

@Repository
public class FpHelpDaoImpl implements FpHelpDao {

    @Resource(name = "hiveJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<FpHelp> getFpHelp(Page page,String errKeyWord) {
        StringBuilder sql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        sql.append("select errcode,errkeyword,errreason,solution from fp_help where syskv='nothing:1' ");
        countSql.append("select count(*) as count from fp_help where syskv='nothing:1' ");
        if (StringUtils.isNotEmpty(errKeyWord)){
            sql.append(" and SEARCH_ALL like '%"+errKeyWord+"%' ");
            countSql.append(" and SEARCH_ALL like '%"+errKeyWord+"%' ");
        }
        if (page != null){
            int total  = 0;
            List<FpHelp> count = jdbcTemplate.query(countSql.toString(),new BeanPropertyRowMapper<>(FpHelp.class));
            if (count != null && count.size()>0 && count.get(0).getCount()!= null){
                total = count.get(0).getCount();
            }
            page.setTotalRows(total);

            sql.append(" ORDER BY errcode  limit "+page.getRowStart()+","+page.getPageSize());

        }

        List<FpHelp> fpHelpList = jdbcTemplate.query(sql.toString(),new BeanPropertyRowMapper<>(FpHelp.class));

        return fpHelpList;
    }
}
