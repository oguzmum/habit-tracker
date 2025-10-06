package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.entity.AppUser;
import com.ozzo.habit_tracker.repository.AppUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AppUser createUser(String username, String rawPassword, String role) {
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(encodePassword(rawPassword));
        appUser.setEnabled(true);
        appUser.setRole(role);
        return appUserRepository.save(appUser);
    }

    public AppUser save(AppUser appUser) {
        if (!isPasswordEncoded(appUser.getPassword())) {
            appUser.setPassword(encodePassword(appUser.getPassword()));
        }
        return appUserRepository.save(appUser);
    }

    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private boolean isPasswordEncoded(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}
