package com.xiaoluo.syservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.system.ApplicationHome;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SyServiceApplication {

    private static final String LOG_HOME_PROPERTY = "LOG_HOME";

    public static void main(String[] args) {
        configureLogHome();
        SpringApplication.run(SyServiceApplication.class, args);
    }

    private static void configureLogHome() {
        if (System.getProperty(LOG_HOME_PROPERTY) != null && !System.getProperty(LOG_HOME_PROPERTY).isBlank()) {
            return;
        }

        ApplicationHome applicationHome = new ApplicationHome(SyServiceApplication.class);
        File homeDir = applicationHome.getDir();
        Path logDir = homeDir.toPath().resolve("logs").normalize();
        try {
            Files.createDirectories(logDir);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Failed to create log directory: " + logDir, ex);
        }
        System.setProperty(LOG_HOME_PROPERTY, logDir.toString());
    }
}
