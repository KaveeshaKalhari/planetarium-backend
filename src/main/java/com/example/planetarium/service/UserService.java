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
        return modelMapper.map(userList, new TypeToken<List<UserDTO>>(){}.getType());
    }

    public LoginResponse createUser(UserDTO userDTO) {
        if (userRepo.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepo.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole("USER");

        User savedUser = userRepo.save(user);

        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getId());

        UserDTO responseDTO = toSafeDTO(savedUser);
        return new LoginResponse(true, "User registered successfully", responseDTO, token);
    }

    public LoginResponse authenticateUser(LoginDTO loginDTO) {
        Optional<User> userOptional = userRepo.findByUsernameOrEmail(loginDTO.getUsernameOrEmail());

        if (userOptional.isEmpty()) {
            return new LoginResponse(false, "Invalid username/email or password", null, null);
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return new LoginResponse(false, "Invalid username/email or password", null, null);
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());

        UserDTO userDTO = toSafeDTO(user);
        return new LoginResponse(true, "Login successful", userDTO, token);
    }

    public UserDTO updateUser(UserDTO userDTO) {
        User existing = userRepo.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setUsername(userDTO.getUsername());
        existing.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userRepo.save(existing);
        return toSafeDTO(existing);
    }

    public UserDTO deleteUser(UserDTO userDTO) {
        userRepo.delete(modelMapper.map(userDTO, User.class));
        return userDTO;
    }

    private UserDTO toSafeDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setPassword(null);
        return dto;
    }
}