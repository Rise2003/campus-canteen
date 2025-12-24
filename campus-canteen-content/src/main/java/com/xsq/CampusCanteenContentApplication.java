package com.xsq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.xsq.content.mapper"})
public class CampusCanteenContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusCanteenContentApplication.class, args);
    }

}
