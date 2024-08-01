package com.ffreitas.taskmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class TaskmasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskmasterApplication.class, args);
    }

}
