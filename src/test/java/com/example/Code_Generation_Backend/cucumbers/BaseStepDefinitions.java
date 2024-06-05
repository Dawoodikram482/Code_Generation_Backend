package com.example.Code_Generation_Backend.cucumbers;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@CucumberContextConfiguration
public class BaseStepDefinitions {
  protected HttpHeaders headers = new HttpHeaders();
  @Autowired
  protected TestRestTemplate restTemplate;

  public static final String VALID_USERNAME = "db@gmail.com";
  public static final String VALID_PASSWORD = "password";
  public static final String INVALID_USERNAME = "brteabtea";
  public static final String INVALID_PASSWORD = "Password1gertabh!";

  public static final String CLIENT_USERNAME = "dawood@gmail.com";
  public static final String CLIENT_PASSWORD = "password";

  public static final String EMPLOYEE_USERNAME = "db@gmail.com";
  public static final String EMPLOYEE_PASSWORD = "password";

  public static final int USER_ID = 3;
}
