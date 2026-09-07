package com.example.javacds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CdsHelperTest {

    @Test
    void testElapsedMsIsNonNegative() {
        long elapsed = CdsHelper.getElapsedMs();
        assertTrue(elapsed >= 0, "Elapsed time should be non-negative");
    }

    @Test
    void testLogCdsStatusDoesNotThrow() {
        assertDoesNotThrow(CdsHelper::logCdsStatus);
    }

    @Test
    void testLogMemoryUsageDoesNotThrow() {
        assertDoesNotThrow(CdsHelper::logMemoryUsage);
    }
}
