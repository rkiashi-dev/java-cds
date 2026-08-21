package com.example.javacds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GracefulShutdownManagerTest {

    @Test
    void shutdownHookRunsWithoutError() {
        Thread hook = GracefulShutdownManager.createShutdownHookThread();
        assertEquals("graceful-shutdown-hook", hook.getName());
        assertDoesNotThrow(hook::run);
    }

    @Test
    void canRegisterShutdownHook() {
        assertDoesNotThrow(GracefulShutdownManager::registerShutdownHook);
    }
}
