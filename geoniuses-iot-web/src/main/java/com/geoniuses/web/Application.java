package com.geoniuses.web;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author liuxin
 * @Date: 2021/5/6 13:07
 * @Description:
 */
@SpringBootApplication
@ComponentScan(value = {"com.geoniuses"})
@MapperScan("com.geoniuses.core.mapper")
public class Application {
    private final static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(Application.class, args);
        } catch (Exception e) {
            logger.error(Application.class.getName(), e);
        }
    }
}
