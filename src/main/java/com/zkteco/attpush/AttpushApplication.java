package com.zkteco.attpush;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zkteco.attpush.mapper")
public class AttpushApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttpushApplication.class, args);
    }


}

