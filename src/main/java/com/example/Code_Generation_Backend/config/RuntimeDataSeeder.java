package com.example.Code_Generation_Backend.config;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.models.*;
import com.example.Code_Generation_Backend.services.AccountService;
import com.example.Code_Generation_Backend.services.TransactionService;
import com.example.Code_Generation_Backend.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_INHOLLAND_BANK_IBAN;

@Component
public class RuntimeDataSeeder implements ApplicationRunner {

  private final UserService userService;
  private final AccountService accountService;
  private final TransactionService transactionService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public RuntimeDataSeeder(UserService userService, AccountService accountService, TransactionService transactionService, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userService = userService;
    this.accountService = accountService;
    this.transactionService = transactionService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) throws Exception {
    seedEmployee();
    User employeeCustomer = seedEmployeeCustomer();
    User customer = seedCustomer();
    User newUser = seedNewUser();
    seedBankAccount();
    secondSeedBankAccount();

    seedTransaction();

  }

  private void seedEmployee() {
    User seedEmployee = User.builder()
        .bsn("123456789")
        .firstName("Dipika")
        .lastName("Bhandari")
        .dateOfBirth(LocalDate.of(2003, 5, 8))
        .phoneNumber("9987654123")
        .email("db@gmail.com")
        .password("password")
        .isActive(true)
        .roles(List.of(Role.ROLE_EMPLOYEE))
        .build();
    userService.SaveUser(seedEmployee);
  }

  private User seedCustomer() {
    User seedCustomer = User.builder()
        .bsn("509547989")
        .firstName("Dawood")
        .lastName("Ikram")
        .dateOfBirth(LocalDate.of(2003, 7, 16))
        .phoneNumber("0611111121")
        .email("dawood@gmail.com")
        .password("password")
        .isActive(true)
        .roles(List.of(Role.ROLE_CUSTOMER))
        .dayLimit(300)
        .transactionLimit(300)
        .build();
    userService.SaveUser(seedCustomer);
    return seedCustomer;
  }

  private User seedEmployeeCustomer() {
    User seedEmployeeCustomer = User.builder()
        .bsn("277545146")
        .firstName("Ugur")
        .lastName("Say")
        .dateOfBirth(LocalDate.of(2005, 1, 1))
        .phoneNumber("0611111111")
        .email("ugur@gmail.com")
        .password(bCryptPasswordEncoder.encode("password"))
        .isActive(true)
        .roles(List.of(Role.ROLE_EMPLOYEE, Role.ROLE_CUSTOMER))
        .dayLimit(1000)
        .transactionLimit(300)
        .build();
    userService.SaveUser(seedEmployeeCustomer);
    return seedEmployeeCustomer;
  }

  private User seedNewUser() {
    User seedNewUser = User.builder()
        .bsn("277545895")
        .firstName("Ugurr")
        .lastName("Say")
        .dateOfBirth(LocalDate.of(2005, 1, 1))
        .phoneNumber("0611111111")
        .email("ugur7@gmail.com")
        .password("password")
        .isActive(true)
        .roles(List.of(Role.ROLE_CUSTOMER))
        .dayLimit(1000)
        .transactionLimit(300)
        .isApproved(false)
        .build();
    userService.SaveUser(seedNewUser);
    return seedNewUser;
  }

  private void seedBankAccount() {
    User inhollandBank = User.builder()
        .bsn("227015277")
        .firstName("Inholland")
        .lastName("Bank")
        .dateOfBirth(LocalDate.now())
        .phoneNumber("680000000000")
        .email("inholland@bank.nl")
        .password("Inholland")
        .isActive(true)
        .transactionLimit(999)
        .dayLimit(999)
        .roles(List.of(Role.ROLE_CUSTOMER))
        .build();

    userService.SaveUser(inhollandBank);
    Account seedAccount = Account.builder()
        .iban(DEFAULT_INHOLLAND_BANK_IBAN)
        .accountBalance(999.0)
        .creationDate(LocalDate.now())
        .absoluteLimit(0)
        .isActive(true)
        .accountType(AccountType.CURRENT)
        .customer(inhollandBank)
        .build();
    accountService.saveAccount(seedAccount);
  }

  private void secondSeedBankAccount() {
    User uniBank = User.builder()
        .bsn("987654321")
        .firstName("Aura")
        .lastName("Alfina")
        .dateOfBirth(LocalDate.of(2000, 1, 1))
        .phoneNumber("9987654123")
        .email("aura@alfina.com")
        .password("password")
        .isActive(true)
        .roles(List.of(Role.ROLE_EMPLOYEE))
        .transactionLimit(99999999)
        .dayLimit(99999999)
        .build();
    userService.SaveUser(uniBank);
    Account seedAccount = Account.builder()
        .iban("NL01UNIB123456789")
        .accountBalance(1000000)
        .creationDate(LocalDate.now())
        .absoluteLimit(0)
        .isActive(true)
        .accountType(AccountType.SAVINGS)
        .customer(uniBank)
        .build();
    accountService.saveAccount(seedAccount);
  }

  private void seedTransaction() {
    User Solaiman = User.builder()
        .bsn("582022290")
        .firstName("Solaiman")
        .lastName("Hossain")
        .dateOfBirth(LocalDate.of(2003, 10, 1))
        .phoneNumber("0611111121")
        .email("Solaiman@hossain.com")
        .password("secretword")
        .isActive(true)
        .dayLimit(300)
        .transactionLimit(300)
        .roles(List.of(Role.ROLE_EMPLOYEE))
        .build();
    userService.SaveUser(Solaiman);

    Account savings = Account.builder()
        .iban("NL01UNIB0000000003")
        .accountBalance(9999.0)
        .isActive(true)
        .accountType(AccountType.SAVINGS)
        .customer(Solaiman)
        .build();

    Account current = Account.builder()
        .iban("NL01UNIB0000000002")
        .accountBalance(2900.0)
        .isActive(true)
        .accountType(AccountType.CURRENT)
        .customer(Solaiman)
        .build();
    accountService.saveAccount(savings);
    accountService.saveAccount(current);

    TransactionDTO newTransaction = new TransactionDTO(10.00, savings.getIban(),
        current.getIban());
    transactionService.addTransaction(newTransaction, Solaiman);

  }
}
