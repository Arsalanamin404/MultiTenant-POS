package com.arsalan.tenanttable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TenanttableApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenanttableApiApplication.class, args);
    }

}
