package com.ecommerce.airlineproject.controller;

import com.ecommerce.airlineproject.dto.AuthRequestDTO;
import com.ecommerce.airlineproject.dto.TransactionStatusDTO;
import com.ecommerce.airlineproject.entity.User;
import com.ecommerce.airlineproject.repository.UserRepository;
import com.ecommerce.airlineproject.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // 1. SİSTEME KAYIT OLMA (REGISTER)
    @Operation(summary = "Register a new user", description = "Pre-Condition: The provided username must not exist in the database.<br>Post-Condition: A new user is created with the ROLE_USER permission and saved to the database.")
    @PostMapping("/register")
    public ResponseEntity<TransactionStatusDTO> register(@RequestBody AuthRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(new TransactionStatusDTO("Failed", "Username is already taken!"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER"); // Sisteme kayıt olan herkes standart kullanıcıdır

        userRepository.save(user);
        return ResponseEntity.ok(new TransactionStatusDTO("Success", "User registered successfully"));
    }

    // 2. SİSTEME GİRİŞ YAPMA VE TOKEN ALMA (LOGIN)
    @Operation(summary = "Login and obtain a JWT token", description = "Pre-Condition: The user must exist and the password must match the database record.<br>Post-Condition: A valid JWT token with user details is generated and returned to the client.")
    @PostMapping("/login")
    public ResponseEntity<TransactionStatusDTO> login(@RequestBody AuthRequestDTO request) {

        java.util.Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isPresent() && passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {

            User user = userOpt.get();
            // JwtUtil makinemizi çalıştırıp Token üretiyoruz
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            // Müşteriye Token'ı veriyoruz
            return ResponseEntity.ok(new TransactionStatusDTO("Success", token));
        }

        // Şifre yanlışsa 401 error code
        return ResponseEntity.status(401).body(new TransactionStatusDTO("Failed", "Invalid username or password"));
    }
}