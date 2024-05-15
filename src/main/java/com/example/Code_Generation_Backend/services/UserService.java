package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.repositories.UserRepository;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.RegisterDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.function.Function;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    private final Function<RegisterDTO, User> registerDTOUserFunction = registerationDTO -> User.builder()
            .bsn(registerationDTO.bsn())
            .email(registerationDTO.email())
            .password(registerationDTO.password())
            .firstName(registerationDTO.firstName())
            .lastName(registerationDTO.lastName())
            .phoneNumber(registerationDTO.phoneNumber())
            .dateOfBirth(LocalDate.parse(registerationDTO.dateOfBirth()))
            .build();
    public User SaveUser(User user){
        return userRepository.save(user);
    }
}
