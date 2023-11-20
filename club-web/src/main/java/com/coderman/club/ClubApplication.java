package com.coderman.club;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ：zhangyukang
 * @date ：2023/11/20 11:30
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.coderman.*.dao"})
public class ClubApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClubApplication.class,args);
    }
}
