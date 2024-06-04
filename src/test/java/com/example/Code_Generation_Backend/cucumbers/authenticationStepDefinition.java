package com.example.Code_Generation_Backend.cucumbers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.LoginRequestDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TokenDTO;
import com.example.Code_Generation_Backend.config.RestTemplateConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class authenticationStepDefinition extends BaseStepDefinitions{
  private final ObjectMapper objectMapper = new ObjectMapper();
  private LoginRequestDTO loginRequest;
  public static String LOGIN_ENDPOINT = "http://localhost:8080/login";



  @Given("I have a valid login credentials")
  public void iHaveAValidLoginCredentials() {
    loginRequest = new LoginRequestDTO(VALID_USERNAME, VALID_PASSWORD);
  }
  @Given("I have a valid user login credentials")
  public void iHaveAValidUserLoginCredentials() {
    loginRequest = new LoginRequestDTO(CLIENT_USERNAME, CLIENT_PASSWORD);
  }
  @Given("I have a valid employee login credentials")
  public void iHaveAValidEmployeeLoginCredentials() {
    loginRequest = new LoginRequestDTO(EMPLOYEE_USERNAME, EMPLOYEE_PASSWORD);
  }
  @When("I call the application login endpoint")
  public void iCallTheApplicationLoginEndpoint() {
    headers.setContentType(MediaType.APPLICATION_JSON);
   testInstanceStore.getInstance().setResponse(restTemplate.exchange(
            LOGIN_ENDPOINT,
            HttpMethod.POST,
            new HttpEntity<>(loginRequest, headers),
            String.class
    ));
  }
  @Then("I receive a token")
  public void iReceiveAToken() throws JsonProcessingException {
    testInstanceStore.getInstance().setToken(
            objectMapper.readValue(testInstanceStore.getInstance().getResponse().getBody().toString(),
                    TokenDTO.class
            ));
  }
}
