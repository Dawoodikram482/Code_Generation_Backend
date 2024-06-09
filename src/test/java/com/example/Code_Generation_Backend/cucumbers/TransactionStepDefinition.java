package com.example.Code_Generation_Backend.cucumbers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.ATMTransactionDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.LoginRequestDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.models.CurrencyType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Objects;

public class TransactionStepDefinition extends BaseStepDefinitions{
  public static final String TRANSACTION_ENDPOINT = "http://localhost:8080/transactions";
  HttpHeaders headers = new HttpHeaders();
  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  private ResponseEntity<String> response;
  private String token;

  @Given("I login with user credentials")
  public void iLoginWithUserCredentials() throws JsonProcessingException {
    headers.clear();
    headers.add("Content-Type", "application/json");
    LoginRequestDTO loginRequestDTO = new LoginRequestDTO("db@gmail.com", "password");
    token = getToken(loginRequestDTO);
  }
}
