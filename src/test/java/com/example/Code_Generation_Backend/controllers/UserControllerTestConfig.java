package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class UserControllerTestConfig {

    @Bean
    @Primary
    public UserService userService() {
        return mock(UserService.class);
    }

    @Bean
    public CommandLineRunner dataSeeder() {
        return args -> {
            // No-op: Override data seeder to prevent runtime data seeding during tests
        };
}}
