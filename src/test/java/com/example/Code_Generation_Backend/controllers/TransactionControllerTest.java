package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponseDTO;
import com.example.Code_Generation_Backend.config.ApiTestConfig;
import com.example.Code_Generation_Backend.models.*;
import com.example.Code_Generation_Backend.services.AccountService;
import com.example.Code_Generation_Backend.services.TransactionService;
import com.example.Code_Generation_Backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
@Import(ApiTestConfig.class)
@EnableMethodSecurity
class TransactionControllerTest {

  @Autowired
  private MockMvc mockMvc;
  private Transaction testTransaction;
  private Transaction testTransaction2;
  private TransactionResponseDTO testTransactionResponseDTO;
  private Account testAccount;
  private Account testAccount2;
  private Account testAccount3;
  private User user;
  private User user2;

  @MockBean
  private TransactionService transactionService;
  @MockBean
  private AccountService accountService;
  @MockBean
  private UserService userService;

  @BeforeEach
  void init(){
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

    testAccount = new Account("NL01UNIB123456789",1000.0,LocalDate.now(),500.0,true, AccountType.CURRENT, user);
  }
}