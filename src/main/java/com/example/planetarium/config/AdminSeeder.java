package com.example.planetarium.config;

import com.example.planetarium.model.User;
import com.example.planetarium.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepo.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@planetarium.lk");
            admin.setPassword(passwordEncoder.encode("Admin@1234"));
            admin.setRole("ADMIN");
            userRepo.save(admin);
            System.out.println("[AdminSeeder] Admin account created: username=admin password=Admin@1234");
        } else {
            System.out.println("[AdminSeeder] Admin account already exists — skipping.");
        }
    }
}