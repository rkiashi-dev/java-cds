package com.example.javacds;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ApplicationLifecycleListenerTest {

    @Test
    void logsWhenApplicationIsReady() {
        ApplicationLifecycleListener listener = new ApplicationLifecycleListener();
        Logger logger = (Logger) LoggerFactory.getLogger(ApplicationLifecycleListener.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        ApplicationReadyEvent event = new ApplicationReadyEvent(
                new SpringApplication(JavaCdsApplication.class),
                new String[0],
                mock(ConfigurableApplicationContext.class),
                Duration.ofMillis(1));

        listener.onApplicationReady(event);

        assertTrue(appender.list.stream().anyMatch(e -> e.getFormattedMessage().contains("startup complete")));
        logger.detachAppender(appender);
    }

    @Test
    void logsWhenApplicationFails() {
        ApplicationLifecycleListener listener = new ApplicationLifecycleListener();
        Logger logger = (Logger) LoggerFactory.getLogger(ApplicationLifecycleListener.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        RuntimeException exception = new RuntimeException("boom");
        ApplicationFailedEvent event = new ApplicationFailedEvent(
                new SpringApplication(JavaCdsApplication.class),
                new String[0],
                null,
                exception);

        listener.onApplicationFailed(event);

        assertTrue(appender.list.stream().anyMatch(e -> e.getFormattedMessage().contains("startup failure")));
        logger.detachAppender(appender);
    }

    @Test
    void defaultUncaughtExceptionHandlerCatchesException() {
        assertDoesNotThrow(() -> {
            GlobalExceptionHandler.install();
            Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
            handler.uncaughtException(Thread.currentThread(), new RuntimeException("unexpected"));
        });
    }
}
