package com.hcms.auth.service;

import com.hcms.auth.dto.AuthResponse;
import com.hcms.auth.dto.LoginRequest;
import com.hcms.auth.dto.RegisterRequest;
import com.hcms.auth.entity.Role;
import com.hcms.auth.entity.User;
import com.hcms.auth.exception.BadRequestException;
import com.hcms.auth.exception.UnauthorizedException;
import com.hcms.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.EMPLOYEE)
                .active(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getUsername(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getEmailOrUsername());

        Optional<User> userOpt = userRepository.findByEmail(request.getEmailOrUsername())
                .or(() -> userRepository.findByUsername(request.getEmailOrUsername()));

        if (userOpt.isEmpty()) {
            log.warn("Login failed: User not found - {}", request.getEmailOrUsername());
            throw new UnauthorizedException("Invalid credentials");
        }

        User user = userOpt.get();

        if (!user.getActive()) {
            log.warn("Login failed: User is inactive - {}", user.getEmail());
            throw new UnauthorizedException("Account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid password for user - {}", user.getEmail());
            throw new UnauthorizedException("Invalid credentials");
        }

        log.info("Login successful for user: {}", user.getEmail());

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getUsername(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}

