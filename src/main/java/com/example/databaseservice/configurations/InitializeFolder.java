package com.example.databaseservice.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class InitializeFolder {
    @Value("${IMAGES_PATH}")
    private String IMAGES_PATH;

    @EventListener(ApplicationReadyEvent.class)
    public void createRootFolder() {
        try {
            File root = new File(IMAGES_PATH);
            if (root.exists()) return;
            root.mkdir();
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize base folder");
        }
    }
}
