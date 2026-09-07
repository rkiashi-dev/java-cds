package com.example.javacds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * Helper class for detecting and logging Java Class Data Sharing (CDS) status.
 */
public class CdsHelper {

    private static final Logger logger = LoggerFactory.getLogger(CdsHelper.class);

    private static final long START_TIME_MS = System.currentTimeMillis();

    private CdsHelper() {
    }

    /**
     * Logs whether CDS is enabled based on JVM arguments.
     */
    public static void logCdsStatus() {
        List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        boolean useSharedArchive = jvmArgs.stream()
                .anyMatch(arg -> arg.contains("SharedArchiveFile") || arg.contains("UseAppCDS"));
        boolean archiveDump = jvmArgs.stream()
                .anyMatch(arg -> arg.contains("ArchiveClassesAtExit") || arg.contains("DumpLoadedClassList"));

        if (useSharedArchive) {
            logger.info("[CDS] CDS is ENABLED - shared archive is in use");
        } else if (archiveDump) {
            logger.info("[CDS] CDS archive GENERATION mode - dumping class list/archive");
        } else {
            logger.info("[CDS] CDS is DISABLED - running without shared archive");
        }

        logCdsJvmArgs(jvmArgs);
    }

    private static void logCdsJvmArgs(List<String> jvmArgs) {
        jvmArgs.stream()
                .filter(arg -> arg.contains("CDS") || arg.contains("Shared") || arg.contains("Archive")
                        || arg.contains("DumpLoaded"))
                .forEach(arg -> logger.info("[CDS] JVM arg: {}", arg));
    }

    /**
     * Returns elapsed milliseconds since class was loaded (approximates startup time).
     */
    public static long getElapsedMs() {
        return System.currentTimeMillis() - START_TIME_MS;
    }

    /**
     * Logs current heap memory usage.
     */
    public static void logMemoryUsage() {
        Runtime rt = Runtime.getRuntime();
        long usedMb = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long totalMb = rt.totalMemory() / (1024 * 1024);
        long maxMb = rt.maxMemory() / (1024 * 1024);
        logger.info("[Memory] used={}MB total={}MB max={}MB", usedMb, totalMb, maxMb);
    }
}
