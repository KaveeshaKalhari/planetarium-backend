package com.example.planetarium.service;

import com.example.planetarium.dto.LoginDTO;
import com.example.planetarium.dto.LoginResponse;
import com.example.planetarium.dto.UserDTO;
import com.example.planetarium.model.User;
import com.example.planetarium.repo.UserRepo;
import com.example.planetarium.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public List<UserDTO> getAllUsers() {
        List<User> userList = userRepo.findAll();
        return modelMapper.map(userList,new TypeToken<List<UserDTO>>(){}.getType());
    }

    public LoginResponse createUser(UserDTO userDTO) {
        // Check if username already exists
        if (userRepo.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepo.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Hash the password
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User savedUser = userRepo.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getId());

        // Return DTO without password
        UserDTO responseDTO = modelMapper.map(savedUser, UserDTO.class);
        responseDTO.setPassword(null);

        return new LoginResponse(true, "User registered successfully", responseDTO, token);
    }

    public LoginResponse authenticateUser(LoginDTO loginDTO) {
        // Find user by username or email
        Optional<User> userOptional = userRepo.findByUsernameOrEmail(loginDTO.getUsernameOrEmail());

        if (userOptional.isEmpty()) {
            return new LoginResponse(false, "Invalid username/email or password", null, null);
        }

        User user = userOptional.get();

        // Check password
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return new LoginResponse(false, "Invalid username/email or password", null, null);
        }

        // Create user DTO without password
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setPassword(null);

        return new LoginResponse(true, "Login successful", userDTO, null);
    }

    public UserDTO updateUser(UserDTO userDTO) {
        //upsert operation
        userRepo.save(modelMapper.map(userDTO,User.class));
        return userDTO;
    }

    public UserDTO deleteUser(UserDTO userDTO) {
        userRepo.delete(modelMapper.map(userDTO,User.class));
        return userDTO;
    }
}
