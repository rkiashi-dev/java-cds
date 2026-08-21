package com.example.javacds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaCdsApplication {

    private static final Logger logger = LoggerFactory.getLogger(JavaCdsApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Java CDS Application");
        CdsHelper.logCdsStatus();
        var context = SpringApplication.run(JavaCdsApplication.class, args);
        SpringApplication.exit(context);
    }
}
