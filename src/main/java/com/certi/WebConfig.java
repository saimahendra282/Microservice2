package com.certi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Map CORS configuration to all API endpoints
                        .allowedOrigins("https://sdp-vo1.netlify.app") // Allow your frontend's origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow these HTTP methods
                        .allowedHeaders("Authorization", "Content-Type", "Accept") // Allow specific headers
                        .allowCredentials(true) // Allow credentials (e.g., cookies, Authorization header)
                        .maxAge(3600); // Cache preflight response for 1 hour
            }
        };
    }
}
