package com.hcms.auth.config;

import com.hcms.auth.entity.Role;
import com.hcms.auth.entity.User;
import com.hcms.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("superadmin@hcms.com")) {
            User superAdmin = User.builder()
                    .username("superadmin")
                    .email("superadmin@hcms.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.SUPER_ADMIN)
                    .active(true)
                    .build();
            userRepository.save(superAdmin);
            log.info("Super admin user created: superadmin@hcms.com");
        } else {
            log.info("Super admin user already exists");
        }
    }
}

