package com.example.javacds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLifecycleListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationLifecycleListener.class);

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        logger.info("ApplicationReadyEvent received: startup complete");
    }

    @EventListener
    public void onApplicationFailed(ApplicationFailedEvent event) {
        logger.error("ApplicationFailedEvent received: startup failure", event.getException());
    }
}
