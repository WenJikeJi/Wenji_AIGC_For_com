package com.wenji.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WenjiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WenjiServerApplication.class, args);
    }

}