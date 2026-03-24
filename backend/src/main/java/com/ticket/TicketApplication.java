package com.ticket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 客户工单平台启动类
 *
 * @author Ticket System
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@EnableCaching
@MapperScan("com.ticket.modules.*.mapper")
public class TicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketApplication.class, args);
    }
}
