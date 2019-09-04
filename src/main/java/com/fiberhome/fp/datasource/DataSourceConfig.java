package com.fiberhome.fp.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 数据源配置
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource.hadoop")
public class DataSourceConfig {
    @Value("${spring.datasource.hadoop.jdbc-url}")
    String url;
    @Value("${spring.datasource.hadoop.username}")
    String userName;
    @Value("${spring.datasource.hadoop.password}")
    String password;
    @Value("${spring.datasource.hadoop.driver-class-name}")
    String drive;


    /***
     * mysql数据源配置
     */
    @Bean(name ="mysqlDataSource")
    @Qualifier("mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource mysqlDataSource(){
        return DataSourceBuilder.create().build();
    }

    /***
     * fp数据源配置
     * @return
     */
    @Bean(name ="hiveDataSource")
    @Qualifier("hiveDataSource")
    @Primary//优先注入
    public DataSource hiveDataSource(){
        //该处只能这样一个一个配置，否则报连接失败
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(drive);
        return dataSource;
    }

    @Bean(name = "mysqlJdbcTemplate")
    public JdbcTemplate mysqlJdbcTemplate(@Qualifier("mysqlDataSource")DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "hiveJdbcTemplate")
    public JdbcTemplate hiveJdbcTemplate(@Qualifier("hiveDataSource")DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }



    @Bean(name = "mysqlNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate mysqlNamedParameterJdbcTemplate(@Qualifier("mysqlDataSource")DataSource dataSource){
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "hiveNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate hiveNamedParameterJdbcTemplate(@Qualifier("hiveDataSource")DataSource dataSource){
        return new NamedParameterJdbcTemplate(dataSource);
    }


}
