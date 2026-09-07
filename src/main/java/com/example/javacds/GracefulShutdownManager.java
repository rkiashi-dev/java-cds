package com.example.javacds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GracefulShutdownManager {

    private static final Logger logger = LoggerFactory.getLogger(GracefulShutdownManager.class);

    private GracefulShutdownManager() {
    }

    static Thread createShutdownHookThread() {
        return new Thread(() -> logger.info("Shutdown hook executed: graceful shutdown complete"), "graceful-shutdown-hook");
    }

    public static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(createShutdownHookThread());
    }
}
