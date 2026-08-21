package com.example.javacds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class CliRunner implements CommandLineRunner, ExitCodeGenerator {

    private static final Logger logger = LoggerFactory.getLogger(CliRunner.class);

    private int exitCode = ExitCode.SUCCESS.getCode();

    @Override
    public void run(String... args) {
        try {
            logger.info("Starting CLI application");
            System.out.println("Hello World");
            logger.info("CLI application completed successfully");
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            exitCode = ExitCode.ERROR.getCode();
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
