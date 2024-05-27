package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.LoginRequestDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.LoginResponseDTO;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.repositories.AuthRepository;
import com.example.Code_Generation_Backend.security.JwtProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(AuthRepository authRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtProvider jwtProvider) {
        this.authRepository = authRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public User findByEmail(String email) {
        return authRepository.findByEmail(email).orElse(null);
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) throws AuthenticationException {
        User user = authRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new AuthenticationException("User not found"));
        if (!bCryptPasswordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new AuthenticationException("Invalid username/password");
        }
        String token = jwtProvider.createToken(user.getEmail(), user.getRoles());
        LoginResponseDTO response = new LoginResponseDTO(user.getEmail(), token, user.getRole(), user.getFirstName(), user.getLastName());
        return response;
    }
}
