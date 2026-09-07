package com.example.javacds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class CliRunner implements CommandLineRunner, ExitCodeGenerator {

    private static final Logger logger = LoggerFactory.getLogger(CliRunner.class);

    private int exitCode = 0;

    @Override
    public void run(String... args) {
        try {
            logger.info("Hello, World! - Spring Boot CLI with Java CDS");
            CdsHelper.logMemoryUsage();
            logger.info("[Startup] Application ready in {}ms", CdsHelper.getElapsedMs());
        } catch (Exception e) {
            logger.error("Error during execution", e);
            exitCode = 1;
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
