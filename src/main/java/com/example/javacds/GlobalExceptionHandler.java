package com.example.javacds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private GlobalExceptionHandler() {
    }

    public static void install() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
                logger.error("Unhandled exception caught by default handler. thread={}", thread.getName(), throwable));
    }
}
