package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.AbsoluteLimitRequestDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.AccountCreatingDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionAccountDTO;
import com.example.Code_Generation_Backend.generators.IBANGenerator;
import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.AccountType;
import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.LongFunction;

import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_CURRENT_ACCOUNT_LIMIT;
import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_SAVINGS_ACCOUNT_LIMIT;

@Service
public class AccountService {


  private final AccountRepository accountRepository;
  private final UserService userService;
  private final IBANGenerator ibanGenerator;

  public AccountService(AccountRepository accountRepository, @Lazy UserService userService) {
    this.accountRepository = accountRepository;
    this.userService = userService;
    this.ibanGenerator = new IBANGenerator(new Random());
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
    String iban = generateUniqueIBAN();
    return Account.builder()
        .accountType(AccountType.valueOf(accountCreatingDTO.accountType().toUpperCase()))
        .customer(user)
        .build();
  }


  public boolean closeAccount(String iban) {
    try {
      Account account = accountRepository.findByIban(iban).orElseThrow(() -> new EntityNotFoundException("Account with iban: " + iban + " not found"));
      account.setActive(false);
      accountRepository.save(account);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Page<TransactionAccountDTO> getIbansByName(Pageable pageable, String firstName, String lastName) throws AccountNotFoundException {
    Page<Account> accounts = accountRepository.findByCustomerFirstNameAndCustomerLastName(pageable, firstName, lastName);
    if (accounts.isEmpty()) {
      throw new AccountNotFoundException("No account found with name: " + firstName + " " + lastName);
    }

    return accounts.map(account -> new TransactionAccountDTO(account.getIban(), account.getAccountType(), account.getCustomer().getFullName()));
  }


  private String generateUniqueIBAN() {
    String iban;
    do {
      iban = (String) ibanGenerator.generate(null, new Account());
    } while (accountRepository.existsByIban(iban));
    return iban;
  }

  public Account updateAbsoluteLimit(Account account, AbsoluteLimitRequestDTO absoluteLimitRequest) {
    if (account.getAccountType().equals(AccountType.SAVINGS)) {
      throw new IllegalArgumentException("Cannot update absolute limit for saving account");
    }
    account.setAbsoluteLimit(absoluteLimitRequest.absoluteLimit());
    return accountRepository.save(account);
  }

  public List<Account> getAccountByStatus(Pageable pageable,boolean isActive) throws AccountNotFoundException {
    Page <Account> accounts = accountRepository.findAccountByIsActive(pageable,isActive);
    if (accounts.isEmpty()) {
      throw new AccountNotFoundException("No account found with status: " + isActive);
    }
    return accounts.getContent();
  }
}
