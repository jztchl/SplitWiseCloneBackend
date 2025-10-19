package com.jztchl.splitwiseclonejava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SplitWiseCloneJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SplitWiseCloneJavaApplication.class, args);
    }

}
