package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.AccountCreatingDTO;
import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.AccountType;
import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.repositories.AccountRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import javax.security.auth.login.AccountNotFoundException;


import java.util.List;
import java.util.function.LongFunction;
import javax.security.auth.login.AccountNotFoundException;

import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_CURRENT_ACCOUNT_LIMIT;
import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_SAVINGS_ACCOUNT_LIMIT;

@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserService userService;

  public AccountService(AccountRepository accountRepository,@Lazy UserService userService) {
    this.accountRepository = accountRepository;
    this.userService = userService;
  }

  // this method is used for internal working with app No new account should be made from user side
  // because it doesn't check account creation limit
  public void saveAccount(Account account) {
    accountRepository.save(account);
  }

  @NonNull
  public List<Account> getAllAccounts(int limit, int offset, AccountType passingAccountType) {
    PageRequest pageRequest = PageRequest.of(offset / limit, limit);
    Page<Account> accounts;
    if (passingAccountType != null) {
      accounts = accountRepository.findByAccountType(passingAccountType, pageRequest);
    } else {
      accounts = accountRepository.findAll(pageRequest);
    }
    return accounts.getContent();
  }

  public Account getAccountByIBAN(String iban) throws AccountNotFoundException {
    return accountRepository.findByIban(iban.toUpperCase()).orElseThrow(() -> new AccountNotFoundException("Account not found."));
  }

  public Account createAccount(AccountCreatingDTO accountCreatingDTO) {
    if (AccountType.valueOf(accountCreatingDTO.accountType().toUpperCase()).equals(AccountType.CURRENT)) {
      checkUserAccountCount(AccountType.CURRENT, accountCreatingDTO.accountHolderId(), DEFAULT_CURRENT_ACCOUNT_LIMIT);
    } else {
      checkUserAccountCount(AccountType.SAVINGS, accountCreatingDTO.accountHolderId(), DEFAULT_SAVINGS_ACCOUNT_LIMIT);
    }
    Account account = createAccountFromDto(accountCreatingDTO);
    userService.SaveUser(account.getCustomer());
    account.getCustomer().addRole(Role.ROLE_CUSTOMER);
    return accountRepository.save(account);
  }

  private void checkUserAccountCount(AccountType type, long Id, int limit) {
    int accountCount = accountRepository.countAccountByCustomer_IdEqualsAndAccountTypeEquals(Id, type);
    LongFunction<Boolean> checkLimit = count -> count >= limit;
    if (Boolean.TRUE.equals(checkLimit.apply((long) accountCount))) {
      throw new IllegalStateException("User has reached the limit of accounts of type " + type);
    }
  }

  private Account createAccountFromDto(AccountCreatingDTO accountCreatingDTO) {
    User user = userService.getUserById(accountCreatingDTO.accountHolderId());
    user.setDayLimit(accountCreatingDTO.dayLimit());
    user.setTransactionLimit(accountCreatingDTO.transactionLimit());
    return Account.builder()
        .accountType(AccountType.valueOf(accountCreatingDTO.accountType().toUpperCase()))
        .customer(user)
        .build();
  }


  /*public Account getIbanByName(String firstName, String lastName) throws AccountNotFoundException {
    return accountRepository.findByCustomerFirstNameAndCustomerLastName(firstName, lastName)
            .orElseThrow(() -> new AccountNotFoundException("User does not exist"));
  }*/

  public String getIbanByName(String firstName, String lastName) throws AccountNotFoundException {
    Account account = accountRepository.findByCustomerFirstNameAndCustomerLastName(firstName, lastName)
            .orElseThrow(() -> new AccountNotFoundException("User does not exist"));
    return account.getIban();
  }



}
