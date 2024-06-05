package com.example.Code_Generation_Backend.cucumbers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.ATMTransactionDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.models.CurrencyType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Objects;

public class TransactionStepDefinition extends BaseStepDefinitions{
  public static final String TRANSACTION_ENDPOINT = "http://localhost:8080/transactions";

  @When("I call the application transaction endpoint")
  public void iCallTheApplicationTransactionEndpoint() {
    headers.setContentType(MediaType.APPLICATION_JSON);

    if (testInstanceStore.getInstance().getToken() != null) {
      headers.setBearerAuth(testInstanceStore.getInstance().getToken().Token());
    }

    testInstanceStore.getInstance().setResponse(restTemplate.exchange(
      TRANSACTION_ENDPOINT,
      HttpMethod.GET,
      new HttpEntity<>(null, headers),
      String.class
    ));
  }
  @Given("I call the application create transaction endpoint with amount {double}, fromAccount {string}, toAccount {string}")
  public void iCallTheApplicationCreateTransactionEndpoint(double amount, String currency, String fromAccount, String toAccount) {
    headers.setContentType(MediaType.APPLICATION_JSON);

    TransactionDTO request = new TransactionDTO(amount, fromAccount, toAccount);

    if (testInstanceStore.getInstance().getToken() != null) {
      headers.setBearerAuth(testInstanceStore.getInstance().getToken().Token());
    }

    testInstanceStore.getInstance().setResponse(restTemplate.exchange(
      TRANSACTION_ENDPOINT,
      HttpMethod.POST,
      new HttpEntity<>(request, headers),
      String.class
    ));
  }
  @Given("I call the application create withdraw endpoint with amount {double}, currencyType {string},iban {string}")
  public void iCallTheApplicationCreateWithdrawEndpointWithAmountCurrencyTypeIban(double amount, String currencyType, String iban) {
    headers.setContentType(MediaType.APPLICATION_JSON);
    CurrencyType currency  = null;
    if(Objects.equals(currencyType, "EURO")){
      currency = CurrencyType.EURO;

    }
    ATMTransactionDTO request = new ATMTransactionDTO(amount, currency, iban);

    if (testInstanceStore.getInstance().getToken() != null) {
      headers.setBearerAuth(testInstanceStore.getInstance().getToken().Token());
    }

    testInstanceStore.getInstance().setResponse(restTemplate.exchange(
      TRANSACTION_ENDPOINT + "/withdraw",
      HttpMethod.POST,
      new HttpEntity<>(request, headers),
      String.class
    ));
  }
  @Given("I call the application create deposit endpoint with amount {double}, currencyType {string},iban {string}")
  public void iCallTheApplicationCreateDepositEndpointWithAmountCurrencyTypeIban(double amount, String currencyType, String iban) {
    headers.setContentType(MediaType.APPLICATION_JSON);
    CurrencyType currency  = null;
    if(Objects.equals(currencyType, "EURO")){
      currency = CurrencyType.EURO;

    }
    ATMTransactionDTO request = new ATMTransactionDTO(amount, currency, iban);

    if (testInstanceStore.getInstance().getToken() != null) {
      headers.setBearerAuth(testInstanceStore.getInstance().getToken().Token());
    }

    testInstanceStore.getInstance().setResponse(restTemplate.exchange(
      TRANSACTION_ENDPOINT + "/deposit",
      HttpMethod.POST,
      new HttpEntity<>(request, headers),
      String.class
    ));
  }
}
