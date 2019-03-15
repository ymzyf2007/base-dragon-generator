package com.dragon.config;

import com.dragon.dao.GeneratorDao;
import com.dragon.dao.MySQLGeneratorDao;
import com.dragon.utils.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DBConfig {
    @Value("${dragon.database: mysql}")
    private String database;
    @Autowired
    private MySQLGeneratorDao mySQLGeneratorDao;

    @Bean
    @Primary
    public GeneratorDao getGeneratorDao() {
        if("mysql".equalsIgnoreCase(database)) {
            return mySQLGeneratorDao;
        } /*else if("oracle".equalsIgnoreCase(database)){
            return oracleGeneratorDao;
        } */else {
            throw new BaseException("不支持当前数据库：" + database);
        }
    }

}