package com.ozzo.habit_tracker.config;

import com.ozzo.habit_tracker.entity.AppUser;
import com.ozzo.habit_tracker.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountInitializer.class);

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.admin.username:admin}")
    private String adminUsername;

    @Value("${app.security.admin.password:}")
    private String adminPassword;

    @Value("${app.security.admin.role:ROLE_ADMIN}")
    private String adminRole;

    public AdminAccountInitializer(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminPassword == null || adminPassword.isBlank()) {
            log.error("No admin password configured via 'app.security.admin.password'. Please set a user and password via environment variables.");
            return;
        }

        AppUser adminUser = appUserRepository
                .findByUsername(adminUsername)
                .orElseGet(() -> new AppUser(adminUsername, "", true, adminRole));

        boolean passwordMatches = isPasswordEncoded(adminUser.getPassword())
                && passwordEncoder.matches(adminPassword, adminUser.getPassword());

        if (!passwordMatches) {
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
        }

        adminUser.setEnabled(true);
        adminUser.setRole(adminRole);

        appUserRepository.save(adminUser);
        log.info("Ensured admin user '{}' exists and is up to date.", adminUsername);
    }

    private boolean isPasswordEncoded(String password) {
        if (password == null) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }
}
