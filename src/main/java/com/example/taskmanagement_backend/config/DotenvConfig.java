package com.example.taskmanagement_backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadDotenv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            // Set environment variables for Spring to pick up
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
            
            System.out.println("✅ Dotenv loaded successfully with " + dotenv.entries().size() + " variables");
            
        } catch (Exception e) {
            System.out.println("⚠️ Could not load .env file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}