package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.LoginRequestDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TokenDTO;
import com.example.Code_Generation_Backend.config.ApiTestConfig;
import com.example.Code_Generation_Backend.config.SecurityConfig;
import com.example.Code_Generation_Backend.services.AuthService;
import com.example.Code_Generation_Backend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@Import({ApiTestConfig.class, SecurityConfig.class})
@EnableMethodSecurity
class AuthControllerTest {


  @MockBean
  private AuthService authService;
  @MockBean
  private AuthController authController;
  @Autowired
  private ObjectMapper objectMapper;
  @Mock
  private AccountController accountController;
  @Mock
  private UserService userService;

  @Test
  void login_Success() throws Exception {
    // Mocking the service response
    when(authService.login(any(LoginRequestDTO.class))).thenReturn("dummy_token");

    // Creating a test controller instance
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

    // Creating a login request DTO
    LoginRequestDTO requestDTO = new LoginRequestDTO("test@example.com", "password123");

    // Performing the request and verifying the response
    mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("dummy_token"));
  }

  @Test
  void login_Failure_UserNotFound() throws Exception {
    // Mocking the service to throw AuthenticationException
    when(authService.login(any(LoginRequestDTO.class))).thenThrow(new IllegalArgumentException("User not found"));

    // Creating a test controller instance
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

    // Performing the request and verifying the response
    mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void login_Failure_IncorrectPassword() throws Exception {
    // Mocking the service to throw AuthenticationException
    when(authService.login(any(LoginRequestDTO.class))).thenThrow(new IllegalArgumentException("Incorrect password"));

    // Creating a test controller instance
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

    // Performing the request and verifying the response
    mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }
}