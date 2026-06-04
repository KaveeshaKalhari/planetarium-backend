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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    public LoginResponse authenticateWithGoogle(String idTokenString) {
        try {
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
            String email  = payload.getEmail();
            String name   = (String) payload.get("name");
            // googleId (payload.getSubject()) removed — was assigned but never used

            Optional<User> existingUser = userRepo.findByEmail(email);
            User user;

            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setUsername(generateUniqueUsername(name, email));
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                // FIX: always set role for Google-authenticated users too
                user.setRole("USER");

                try {
                    user = userRepo.save(user);
                } catch (Exception e) {
                    Optional<User> retry = userRepo.findByEmail(email);
                    if (retry.isPresent()) {
                        user = retry.get();
                    } else {
                        throw e;
                    }
                }
            }

            // FIX: pass role to generateToken so it's embedded in the JWT claim
            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());

            // Build response DTO safely — no ModelMapper so role is never nulled
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setRole(user.getRole());
            userDTO.setPassword(null);

            return new LoginResponse(true, "Google authentication successful", userDTO, token);

        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResponse(false, "Google authentication failed: " + e.getMessage(), null, null);
        }
    }

    private String generateUniqueUsername(String name, String email) {
        String baseUsername;
        if (name != null && !name.trim().isEmpty()) {
            baseUsername = name.toLowerCase()
                    .replaceAll("\\s+", "")
                    .replaceAll("[^a-z0-9]", "");
        } else {
            baseUsername = email.split("@")[0].replaceAll("[^a-z0-9]", "");
        }

        if (baseUsername.length() < 3)  baseUsername = "user" + baseUsername;
        if (baseUsername.length() > 47) baseUsername = baseUsername.substring(0, 47);

        String username = baseUsername;
        int counter = 1;
        while (userRepo.existsByUsername(username)) {
            username = baseUsername + counter++;
            if (counter > 1000) {
                username = baseUsername + UUID.randomUUID().toString().substring(0, 8);
                break;
            }
        }
        return username;
    }
}