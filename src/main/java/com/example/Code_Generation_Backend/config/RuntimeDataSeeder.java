package com.example.Code_Generation_Backend.config;

import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.AccountType;
import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.services.AccountService;
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
<<<<<<< Updated upstream

  public RuntimeDataSeeder(UserService userService, AccountService accountService) {
    this.userService = userService;
    this.accountService = accountService;
=======
  private final TransactionService transactionService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public RuntimeDataSeeder(UserService userService, AccountService accountService, TransactionService transactionService, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userService = userService;
    this.accountService = accountService;
    this.transactionService = transactionService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
>>>>>>> Stashed changes
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) throws Exception {
    seedEmployee();
    User employeeCustomer = seedEmployeeCustomer();
    User customer = seedCustomer();
    seedBankAccount();
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
        .roles(List.of(Role.ROLE_EMPLOYEE))
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
//  private void SeedBankAccount() {
//    User uniBank = User.builder()
//        .bsn("123456789")
//        .firstName("Aura")
//        .lastName("Alfina")
//        .dateOfBirth(LocalDate.of(2000, 1, 1))
//        .phoneNumber("9987654123")
//        .email("aura@alfina.com")
//        .password("password")
//        .isActive(true)
//        .roles(List.of(Role.ROLE_EMPLOYEE))
//        .transactionLimit(99999999)
//        .dayLimit(99999999)
//        .build();
//    userService.SaveUser(uniBank);
//    Account seedAccount = Account.builder()
//        .iban("NL01UNIB123456789")
//        .accountBalance(1000000)
//        .creationDate(LocalDate.now())
//        .absoluteLimit(0)
//        .isActive(true)
//        .accountType(AccountType.CURRENT)
//        .customer(uniBank)
//        .build();
//    accountService.saveAccount(seedAccount);
//  }
}
