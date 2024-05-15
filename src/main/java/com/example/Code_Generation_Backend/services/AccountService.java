package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.repositories.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
  private final UserService userService;
  private final AccountRepository accountRepository;
  public AccountService(UserService userService, AccountRepository accountRepository) {
    this.userService = userService;
    this.accountRepository = accountRepository;
  }
  // this method is used for internal working with app No new account should be made from user side
  // because it doesn't check account creation limit
  public void saveAccount(Account account) {
    accountRepository.save(account);
  }

}
