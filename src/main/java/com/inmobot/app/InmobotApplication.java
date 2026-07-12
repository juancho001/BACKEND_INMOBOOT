package com.inmobot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.inmobot")
@EntityScan(basePackages = "com.inmobot")
@EnableJpaRepositories(basePackages = "com.inmobot")
public class InmobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(InmobotApplication.class, args);
    }
}
