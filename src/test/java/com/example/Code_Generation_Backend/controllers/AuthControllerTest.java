package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.LoginRequestDTO;
import com.example.Code_Generation_Backend.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.naming.AuthenticationException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AuthService authService;

  @Autowired
  private ObjectMapper objectMapper;

  private LoginRequestDTO loginRequestDTO;
  private String dummyToken;

  @BeforeEach
  public void setup() {
    loginRequestDTO = new LoginRequestDTO("test@example.com", "password123");
    dummyToken = "dummy_token";
  }

  @Test
  void login_Success() throws Exception {
    Mockito.when(authService.login(Mockito.any(LoginRequestDTO.class))).thenReturn(dummyToken);

    mockMvc.perform(MockMvcRequestBuilders.post("/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.Token").value(dummyToken));
  }

  @Test
  void login_Unauthorized() throws Exception {
    Mockito.when(authService.login(Mockito.any(LoginRequestDTO.class))).thenThrow(new AuthenticationException("Incorrect password"));

    mockMvc.perform(MockMvcRequestBuilders.post("/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequestDTO)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$").value("Incorrect password"));
  }
}