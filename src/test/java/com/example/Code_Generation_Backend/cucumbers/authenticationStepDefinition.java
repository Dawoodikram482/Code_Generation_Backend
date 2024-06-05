package com.example.Code_Generation_Backend.cucumbers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.LoginRequestDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TokenDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
@Log
public class authenticationStepDefinition extends BaseStepDefinitions{
  private final ObjectMapper objectMapper = new ObjectMapper();
  private LoginRequestDTO loginRequest;
  public static String LOGIN_ENDPOINT = "http://localhost:8080/login";
  private ResponseEntity<String> response;
  private String token;

  @Given("I have a valid login credentials")
  public void iHaveAValidLoginCredentials() {
    loginRequest = new LoginRequestDTO("db@gmail.com", "password");
  }
//  @Given("I have a valid user login credentials")
//  public void iHaveAValidUserLoginCredentials() {
//    loginRequest = new LoginRequestDTO(CLIENT_USERNAME, CLIENT_PASSWORD);
//  }
//  @Given("I have a valid employee login credentials")
//  public void iHaveAValidEmployeeLoginCredentials() {
//    loginRequest = new LoginRequestDTO(EMPLOYEE_USERNAME, EMPLOYEE_PASSWORD);
//  }
  @When("I call the application login endpoint")
  public void iCallTheApplicationLoginEndpoint() throws JsonProcessingException {
    headers.add("Content-Type", "application/json");
   response = restTemplate.exchange(
            LOGIN_ENDPOINT,
            HttpMethod.POST,
            new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers),
            String.class
    );
    log.info("Response body: " + response.getBody());
  }
  @Then("I receive a token")
  public void iReceiveAToken() throws JsonProcessingException {
    String responseData = response.getBody();
    Assertions.assertNotNull(responseData);
    TokenDTO tokenDTO = objectMapper.readValue(responseData, TokenDTO.class);
    token = tokenDTO.Token();
    Assertions.assertNotNull(token);
  }
}
