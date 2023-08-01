//package com.zkteco.attpush.utils;
//
//import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
//@Configuration
//@MapperScan("com.zkteco.attpush.mapper")
//public class MybatisPlusConfig {
//    @Autowired
//    private DataSource dataSource;
//    @Bean
//    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean() {
//        MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
//        mybatisPlus.setDataSource(dataSource);
//        return mybatisPlus;
//    }
//}
