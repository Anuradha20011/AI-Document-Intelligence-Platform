package com.anuradha.document_intelligence;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return "AI Document Intelligence Platform is Running!";
    }

    @GetMapping("/api/health")
    public String health() {
        return "Backend API is Working Successfully!";
    }

    // Protected API - JWT required
    @GetMapping("/api/user/profile")
    public Map<String, String> userProfile(Authentication authentication) {

        return Map.of(
                "message", "Protected API accessed successfully!",
                "email", authentication.getName()
        );
    }
}