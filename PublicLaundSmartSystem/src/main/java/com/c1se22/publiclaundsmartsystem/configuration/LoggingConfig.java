package com.c1se22.publiclaundsmartsystem.configuration;
import com.c1se22.publiclaundsmartsystem.aspect.LoggingAspect;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.File;


@Configuration
@EnableAspectJAutoProxy
public class LoggingConfig {

    private static final Logger log = LoggerFactory.getLogger(LoggingConfig.class);

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
    
    @PostConstruct
    public void configureLogging() {
        try {
            File logDirectory = new File("logs");
            if (!logDirectory.exists()) {
                boolean created = logDirectory.mkdirs();
                if (!created) {
                    log.error("Failed to create logs directory");
                    return;
                }
            }
            
            File logFile = new File("logs/application.log");
            if (!logFile.exists()) {
                boolean created = logFile.createNewFile();
                if (!created) {
                    log.error("Failed to create application.log file");
                }
            }
        } catch (Exception e) {
            log.error("Error configuring logging: ", e);
        }
    }
} 