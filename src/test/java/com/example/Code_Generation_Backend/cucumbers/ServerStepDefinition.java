package com.example.Code_Generation_Backend.cucumbers;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.List;

public class ServerStepDefinition extends BaseStepDefinitions{
  @Before
  public static void beforeEach(){
    testInstanceStore.getInstance().setToken(null);
    testInstanceStore.getInstance().setResponse(null);
  }
  @Given("The endpoint for {string} is available for method {string}")
  public void theEndpointForIsAvailableForMethod(String endpoint, String method) {
    if (testInstanceStore.getInstance().getToken() != null) {
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(testInstanceStore.getInstance().getToken().Token());
    }
    testInstanceStore.getInstance().setResponse(restTemplate.exchange(
      "/" + endpoint,
      HttpMethod.OPTIONS,
      new HttpEntity<>(null, headers),
      String.class
    ));
    List<String> options = List.of(testInstanceStore.getInstance().getResponse().getHeaders().get("Allow").get(0).replaceAll("]", "").split(","));
    System.out.println("Options: "+ options);
    Assertions.assertTrue(options.contains(method.toUpperCase()));
  }
  @And("I get HTTP status {int}")
  public void iGetHTTPStatus(int code) {
    Assertions.assertEquals(code, testInstanceStore.getInstance().getResponse().getStatusCode().value());
  }
  @And("I get {int} elements in the list")
  public void iGetElementsInTheList(int count) {
    Assertions.assertEquals(count, (Integer) JsonPath.read(testInstanceStore.getInstance().getResponse().getBody().toString(), "$.length()"));
  }
}
