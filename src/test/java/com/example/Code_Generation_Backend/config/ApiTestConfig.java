package com.example.Code_Generation_Backend.config;

import com.example.Code_Generation_Backend.security.JwtProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class ApiTestConfig {
  @MockBean
  private JwtProvider jwtTokenProvider;
}
