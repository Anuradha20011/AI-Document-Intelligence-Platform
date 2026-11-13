package com.anuradha.document_intelligence;

import com.anuradha.document_intelligence.dto.LoginRequest;
import com.anuradha.document_intelligence.dto.RegistrationRequest;
import com.anuradha.document_intelligence.entity.User;
import com.anuradha.document_intelligence.service.UserService;
import com.anuradha.document_intelligence.security.JwtService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Registration API
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegistrationRequest request) {

        try {
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());

            User savedUser = userService.registerUser(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "message", "Registration successful!",
                            "userId", savedUser.getId(),
                            "name", savedUser.getName(),
                            "email", savedUser.getEmail()
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    // Login API with JWT
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Valid @RequestBody LoginRequest request) {

        User user = userService.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        boolean passwordMatches = userService.verifyPassword(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful!",
                        "userId", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "token", token
                )
        );
    }
}