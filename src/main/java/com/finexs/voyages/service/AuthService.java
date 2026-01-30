package com.finexs.voyages.service;

import com.finexs.voyages.dto.AuthRequest;
import com.finexs.voyages.dto.AuthResponse;
import com.finexs.voyages.dto.RegisterRequest;
import com.finexs.voyages.dto.UserDto;
import com.finexs.voyages.entity.User;
import com.finexs.voyages.entity.UserRole;
import com.finexs.voyages.repository.UserRepository;
import com.finexs.voyages.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.TRAVELER);

        User savedUser = userRepository.save(user);
        String token = jwtProvider.generateToken(savedUser.getEmail(), savedUser.getRole().toString());

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                token
        );
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtProvider.generateToken(user.getEmail(), user.getRole().toString());

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }

    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

}
