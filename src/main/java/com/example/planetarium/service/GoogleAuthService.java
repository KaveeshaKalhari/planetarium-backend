package com.example.planetarium.service;

import com.example.planetarium.dto.LoginResponse;
import com.example.planetarium.dto.UserDTO;
import com.example.planetarium.model.User;
import com.example.planetarium.repo.UserRepo;
import com.example.planetarium.util.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class GoogleAuthService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    public LoginResponse authenticateWithGoogle(String idTokenString) {
        try {
            // Verify Google token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                return new LoginResponse(false, "Invalid Google token", null, null);
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            // Get user info from Google
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String googleId = payload.getSubject();

            System.out.println("Google Auth - Email: " + email);
            System.out.println("Google Auth - Name: " + name);

            // Check if user exists
            Optional<User> existingUser = userRepo.findByEmail(email);
            User user;
            String message;

            if (existingUser.isPresent()) {
                // User exists, just login
                user = existingUser.get();
                message = "Login successful";
                System.out.println("Existing user found: " + user.getUsername());
            } else {
                // Create new user
                user = new User();
                user.setEmail(email);
                user.setUsername(generateUniqueUsername(name, email));
                // Set a random password (user won't use it, they'll login with Google)
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

                try {
                    user = userRepo.save(user);
                    message = "Account created successfully";
                    System.out.println("New user created: " + user.getUsername());
                } catch (Exception e) {
                    // If save fails (e.g., duplicate email), try to find existing user again
                    System.err.println("Error saving user: " + e.getMessage());
                    Optional<User> retryFind = userRepo.findByEmail(email);
                    if (retryFind.isPresent()) {
                        user = retryFind.get();
                        message = "Login successful";
                    } else {
                        throw e; // Re-throw if user still not found
                    }
                }
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());

            // Create user DTO without password
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            userDTO.setPassword(null);

            return new LoginResponse(true, "Google authentication successful", userDTO, token);

        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResponse(false, "Google authentication failed: " + e.getMessage(), null, null);
        }
    }

    private String generateUniqueUsername(String name, String email) {
        // Create base username from name or email
        String baseUsername;

        if (name != null && !name.trim().isEmpty()) {
            baseUsername = name.toLowerCase()
                    .replaceAll("\\s+", "")
                    .replaceAll("[^a-z0-9]", "");
        } else {
            baseUsername = email.split("@")[0]
                    .replaceAll("[^a-z0-9]", "");
        }

        // Ensure minimum length of 3 characters
        if (baseUsername.length() < 3) {
            baseUsername = "user" + baseUsername;
        }

        // Ensure maximum length of 50 characters
        if (baseUsername.length() > 47) {
            baseUsername = baseUsername.substring(0, 47);
        }

        // If username exists, append number
        String username = baseUsername;
        int counter = 1;
        while (userRepo.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;

            // Safety check to prevent infinite loop
            if (counter > 1000) {
                username = baseUsername + UUID.randomUUID().toString().substring(0, 8);
                break;
            }
        }

        return username;
    }
}
