package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.repositories.AuthRepository;
import com.example.Code_Generation_Backend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthRepository authRepository;
    public MyUserDetailsService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = authRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return new CustomUserDetails(user);
        } else {
            throw new UsernameNotFoundException(email + " not found");
        }
    }
}
