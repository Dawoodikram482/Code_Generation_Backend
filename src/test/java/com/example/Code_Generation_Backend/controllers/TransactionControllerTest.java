package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.ATMTransactionDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionAccountDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponseDTO;
import com.example.Code_Generation_Backend.config.ApiTestConfig;
import com.example.Code_Generation_Backend.models.*;
import com.example.Code_Generation_Backend.services.AccountService;
import com.example.Code_Generation_Backend.services.TransactionService;
import com.example.Code_Generation_Backend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
@Import(ApiTestConfig.class)
@EnableMethodSecurity
class TransactionControllerTest {

  @Autowired
  private MockMvc mockMvc;
  private Transaction testTransaction;
  private Transaction testWithdrawal;
  private Transaction testDeposit;
  private TransactionResponseDTO testTransactionResponseDTO;
  private Account testAccount;
  private Account testAccount2;
  private Account testAccount3;
  private User user;
  private User user2;
  private ATMTransactionDTO atmTransactionDTO;

  @MockBean
  private TransactionService transactionService;
  @MockBean
  private AccountService accountService;
  @MockBean
  private UserService userService;
  @Autowired
  private TransactionController transactionController;


  @BeforeEach
  void init() {
    user = User.builder()
        .bsn("509547989")
        .firstName("Dawood")
        .lastName("Ikram")
        .dateOfBirth(LocalDate.of(2003, 7, 16))
        .phoneNumber("0611111121")
        .email("dawood@gmail.com")
        .password("password")
        .isActive(true)
        .isApproved(true)
        .roles(List.of(Role.ROLE_CUSTOMER))
        .dayLimit(300)
        .transactionLimit(300)
        .build();

    user2 = User.builder()
        .bsn("123456789")
        .firstName("Dipika")
        .lastName("Bhandari")
        .dateOfBirth(LocalDate.of(2003, 5, 8))
        .phoneNumber("9987654123")
        .email("db@gmail.com")
        .password("password")
        .isActive(true)
        .isApproved(true)
        .roles(List.of(Role.ROLE_EMPLOYEE))
        .dayLimit(300)
        .transactionLimit(300)
        .build();

    testAccount = new Account("NL01UNIB123456789", 1000.0, LocalDate.now(), 500.0, true, AccountType.CURRENT, user);
    testAccount2 = new Account("NL01UNIB0000000003", 500.0, LocalDate.now(), 250, true, AccountType.SAVINGS, user2);
    testAccount3 = new Account("NL01UNIB0000000002", 1000.0, LocalDate.now(), 500, true, AccountType.CURRENT, user2);

    testTransaction = new Transaction(100.0, testAccount3, testAccount2, LocalDate.now(), LocalTime.now(), user2, TransactionType.TRANSFER, CurrencyType.EURO);
    testWithdrawal = new Transaction(100.0, testAccount, null, LocalDate.now(), LocalTime.now(), user2, TransactionType.WITHDRAWAL, CurrencyType.EURO);
    testDeposit = new Transaction(100.0, null, testAccount, LocalDate.now(), LocalTime.now(), user2, TransactionType.DEPOSIT, CurrencyType.EURO);
    testTransactionResponseDTO = new TransactionResponseDTO(testTransaction.getTransactionID(),
        testTransaction.getAmount(),
        new TransactionAccountDTO(testAccount.getIban(), testAccount.getAccountType(), user.getFullName()),
        new TransactionAccountDTO(testAccount3.getIban(), testAccount3.getAccountType(), user2.getFullName()),
        testTransaction.getDate(),
        testTransaction.getTimestamp(),
        testTransaction.getUserPerforming().getEmail(),
        testTransaction.getTransactionType()
    );
  }

  @Test
  @WithMockUser(username = "db@gmail.com", password = "password", roles = {"EMPLOYEE", "CUSTOMER"})
  void creatingTransactionReturns200() throws Exception {
    TransactionDTO transactionDTO = new TransactionDTO(50.0, testAccount.getIban(), testAccount3.getIban());
    when(transactionService.isValidTransaction(transactionDTO)).thenReturn(true);
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(transactionDTO);

    mockMvc.perform(
        MockMvcRequestBuilders.post("/transactions")
            .contentType("application/json")
            .content(requestJson)
            .with(csrf())
    ).andExpect(status().isOk());
  }
  @Test
  @WithMockUser(username = "dawood@gmail.com", password = "password", roles = "CUSTOMER")
  void customerGettingTheirTransactionsReturnsListOfTransactions() throws Exception{
    when(transactionService.getTransactions(transactionController.getPagination(50,0),
        testAccount.getIban())).thenReturn(List.of(testTransactionResponseDTO));
    mockMvc.perform(
        MockMvcRequestBuilders.get("/transactions/account/{iban}", testAccount.getIban())
            .param("limit", "50")
            .param("offset", "0")
            .with(csrf())
    ).andExpect(status().isOk());
  }
  @Test
  @WithMockUser(username = "db@gmail.com", password = "password", roles = "EMPLOYEE")
  void employeeGettingTransactionsOfCustomerReturnsListOfTransactions() throws Exception{
    when(transactionService.getTransactions(transactionController.getPagination(50,0),
        testAccount.getIban())).thenReturn(List.of(testTransactionResponseDTO));
    mockMvc.perform(
        MockMvcRequestBuilders.get("/transactions/account/{iban}", testAccount.getIban())
            .param("limit", "50")
            .param("offset", "0")
            .with(csrf())
    ).andExpect(status().isOk());
  }
  @Test
  @WithMockUser(username = "db@gmail.com", password = "password", roles = "EMPLOYEE")
  void employeeGettingAllTransactionsReturnsListOfTransaction() throws Exception{
    when(transactionService.getAllTransactions(transactionController.getPagination(50,0),
        null, null, null, null, null, null, null, null, user2.getEmail()))
        .thenReturn(List.of(testTransactionResponseDTO));
    mockMvc.perform(
        MockMvcRequestBuilders.get("/transactions")
            .param("limit", "50")
            .param("offset", "0")
            .with(csrf())
    ).andExpect(status().isOk());
  }
  @Test
  @WithMockUser(username = "dawood@gmail.com", password = "password", roles = {"EMPLOYEE", "CUSTOMER"})
  void customerWithdrawingReturns201() throws Exception{
    atmTransactionDTO = new ATMTransactionDTO(100.0,CurrencyType.EURO, testAccount.getIban());
    when(transactionService.withdraw(atmTransactionDTO, user.getFullName()))
        .thenReturn(testWithdrawal);
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(atmTransactionDTO);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/transactions/atm/withdraw")
            .contentType("application/json")
            .content(requestJson)
            .with(csrf())
    ).andExpect(status().isCreated());
  }
  @Test
  @WithMockUser(username = "dawood@gmail.com", password = "password", roles = {"EMPLOYEE", "CUSTOMER"})
  void customerDepositReturns201() throws Exception{
    atmTransactionDTO = new ATMTransactionDTO(100.0,CurrencyType.EURO, testAccount.getIban());
    when(transactionService.Deposit(atmTransactionDTO, user.getFullName()))
        .thenReturn(testDeposit);
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(atmTransactionDTO);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/transactions/atm/deposit")
            .contentType("application/json")
            .content(requestJson)
            .with(csrf())
    ).andExpect(status().isCreated());
  }

}