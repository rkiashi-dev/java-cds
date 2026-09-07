package com.example.javacds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaCdsApplication {

    private static final Logger logger = LoggerFactory.getLogger(JavaCdsApplication.class);

    public static void main(String[] args) {
        CdsHelper.logCdsStatus();
        GlobalExceptionHandler.install();
        GracefulShutdownManager.registerShutdownHook();

        try {
            var context = SpringApplication.run(JavaCdsApplication.class, args);
            int exitCode = SpringApplication.exit(context);
            logger.info("Application exited with code {}", exitCode);
            System.exit(exitCode);
        } catch (Exception ex) {
            logger.error("Application startup failed", ex);
            System.exit(1);
        }
    }
}
