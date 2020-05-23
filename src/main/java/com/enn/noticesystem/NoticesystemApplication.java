package com.enn.noticesystem;

import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.enn.noticesystem.dao.mapper")
public class NoticesystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoticesystemApplication.class, args);
    }

}
