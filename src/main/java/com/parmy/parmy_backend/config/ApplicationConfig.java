package com.parmy.parmy_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class ApplicationConfig {
    // This class enables scheduling for OTP cleanup tasks
}