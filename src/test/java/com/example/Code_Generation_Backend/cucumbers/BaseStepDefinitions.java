package com.example.Code_Generation_Backend.cucumbers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.LoginRequestDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TokenDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@CucumberContextConfiguration
public class BaseStepDefinitions {
  protected HttpHeaders headers = new HttpHeaders();
  protected ResponseEntity<String> response;
  @Autowired
  protected TestRestTemplate restTemplate;

  @Autowired
  protected ObjectMapper objectMapper;

  public static final String VALID_USERNAME = "db@gmail.com";
  public static final String VALID_PASSWORD = "password";
  public static final String INVALID_USERNAME = "brteabtea";
  public static final String INVALID_PASSWORD = "Password1gertabh!";

  public static final String CLIENT_USERNAME = "dawood@gmail.com";
  public static final String CLIENT_PASSWORD = "password";

  public static final String EMPLOYEE_USERNAME = "db@gmail.com";
  public static final String EMPLOYEE_PASSWORD = "password";

  protected String getToken(LoginRequestDTO loginDTO) throws JsonProcessingException {
    response = restTemplate.exchange(
        "http://localhost:8080/login",
        HttpMethod.POST,
        new HttpEntity<>(objectMapper.writeValueAsString(loginDTO), headers), String .class);
    TokenDTO tokenDTO = objectMapper.readValue(response.getBody(), TokenDTO.class);
    return tokenDTO.Token();
  }
}
