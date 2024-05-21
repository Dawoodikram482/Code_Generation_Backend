package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.ATMTransactionDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionAccountDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponseDTO;
import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.AccountType;
import com.example.Code_Generation_Backend.models.Transaction;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.models.exceptions.DailyLimitException;
import com.example.Code_Generation_Backend.models.exceptions.InsufficientBalanceException;
import com.example.Code_Generation_Backend.models.exceptions.TransactionLimitException;
import com.example.Code_Generation_Backend.repositories.AccountRepository;
import com.example.Code_Generation_Backend.repositories.TransactionRepository;
import com.example.Code_Generation_Backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final UserService userService;
  private final UserRepository userRepository;
  private final AccountService accountService;

  public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, UserService userService, UserRepository userRepository, AccountService accountService) {
    this.transactionRepository = transactionRepository;
    this.accountRepository = accountRepository;
    this.userService = userService;
    this.userRepository = userRepository;
    this.accountService = accountService;
  }

  public TransactionResponseDTO addTransaction(TransactionDTO transactionDTO, User initiator) {
    Transaction newTransaction = transactionRepository.save(createTransactionFromDto(transactionDTO, initiator));
    return createDto(newTransaction);
  }

  public void updateAccountBalance(Account account, double amount, boolean isDeposit) {
    double balance = account.getAccountBalance();
    if (isDeposit) {
      account.setAccountBalance(balance + amount);
    } else {
      account.setAccountBalance(balance - amount);
    }
    accountService.saveAccount(account);
  }

  public Transaction processTransaction(TransactionDTO transactionDTO) {
    User user = createDummyUserForTransactions();
    Transaction newTransaction = transactionRepository.save(createTransactionFromDto(transactionDTO, user));
    return newTransaction;
  }

  public void processATMTransaction(ATMTransactionDTO atmTransactionDTO) {
    Account account = accountRepository.findById(atmTransactionDTO.account()).orElseThrow(() -> new EntityNotFoundException("Account with iban: " + atmTransactionDTO.account() + " not found."));

    if (atmTransactionDTO.action().equals("deposit")) {
      increaseBalance(atmTransactionDTO.amount(), atmTransactionDTO.account());
    } else if (atmTransactionDTO.action().equals("withdraw")) {
      // check if the account has enough balance  to withdraw

      if (account.getAccountBalance() < atmTransactionDTO.amount()) {
        throw new InsufficientBalanceException("Insufficient balance");
      }

      decreaseBalance(atmTransactionDTO.amount(), atmTransactionDTO.account());
    }
  }

  public List<TransactionResponseDTO> getTransactions(Pageable pageable, String iban) {
    List<Transaction> transactions = transactionRepository.getTransactionsByCustomer(pageable, iban).getContent();
    if (transactions.isEmpty()) {
      throw new EntityNotFoundException("No transactions found for account with iban: " + iban);
    }
    List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();
    transactions.forEach(transaction -> transactionResponseDTOS.add(createDto(transaction)));
    return transactionResponseDTOS;
  }

  public void changeBalance(double amount, String accountFrom, String accountTo) {
    decreaseBalance(amount, accountFrom);
    increaseBalance(amount, accountTo);
  }

  public void increaseBalance(double amount, String accountTo) {
    transactionRepository.increaseBalanceByAmount(amount, accountTo);
  }

  public void decreaseBalance(double amount, String accountFrom) {
    transactionRepository.decreaseBalanceByAmount(amount, accountFrom);
  }

  public boolean isValidTransaction(TransactionDTO transactionDTO) {
    Account accountFrom = accountRepository.findById(transactionDTO.accountFrom()).orElseThrow(()
        -> new EntityNotFoundException("Account with iban: " + transactionDTO.accountFrom() + " not found."));
    Account accountTo = accountRepository.findById(transactionDTO.accountTo()).orElseThrow(()
        -> new EntityNotFoundException("Account with iban: " + transactionDTO.accountTo() + " not found."));
    validateAccountAndTransferConditions(transactionDTO, accountFrom, accountTo);
    validateLimits(accountFrom, accountTo, transactionDTO.amount());
    return true;
  }

  private void validateAccountAndTransferConditions(TransactionDTO dto, Account accountFrom, Account accountTo) {
    if (dto.amount() <= 0) {
      throw new IllegalArgumentException("Amount must be a positive number");
    }
    if ((!accountTo.isActive() || !accountFrom.isActive())) {
      throw new IllegalArgumentException("You cannot transfer to or from an inactive account");
    }
    if (((accountFrom.getAccountType() == AccountType.CURRENT && accountTo.getAccountType() == AccountType.SAVINGS) ||
        (accountFrom.getAccountType() == AccountType.SAVINGS && accountTo.getAccountType() == AccountType.CURRENT))
        && !accountTo.getCustomer().getBsn().equals(accountFrom.getCustomer().getBsn())) {
      throw new IllegalArgumentException("You cannot transfer from a current account to a savings account");
    }
    if (accountFrom.getAccountBalance() - dto.amount() < accountFrom.getAbsoluteLimit()) {
      throw new InsufficientBalanceException("The amount you are trying to transfer will result in a balance lower than the absolute limit for this account\"");
    } else if (dto.amount() > accountFrom.getAccountBalance()) {
      throw new InsufficientBalanceException("Insufficient balance");
    }
  }

  private void validateLimits(Account accountFrom, Account accountTo, double amount) {
    if (accountFrom.getAbsoluteLimit() < amount) {
      if (accountTo == null || (accountTo.getAccountType() != AccountType.SAVINGS && accountFrom.getAccountType() != AccountType.SAVINGS)) {
        throw new TransactionLimitException("Transaction limit exceeded");
      }
    }
    if(accountFrom.getCustomer().getDayLimit()<amount){
      throw new DailyLimitException("Cannot exceed daily transaction limit");
    }
    if (accountFrom.getAccountType() == AccountType.SAVINGS && accountFrom.getCustomer().getId() == accountTo.getCustomer().getId()) {
      return;
    }
    if (accountFrom.getAccountBalance() < amount) {
      if (accountTo == null || (accountTo.getAccountType() != AccountType.SAVINGS && accountFrom.getAccountType() != AccountType.SAVINGS)) {
        throw new InsufficientBalanceException("Insufficient balance");
      }
    }
    if (accountTo.getAccountType() != AccountType.SAVINGS && amount + getSumOfMoneyTransferredToday(accountFrom.getCustomer().getEmail()) > accountFrom.getCustomer().getDayLimit()) {
      throw new DailyLimitException("The transaction that you are going to make will exceed your daily limit");
    }
  }
  //  public void checkUserLimits(Account accountFrom, Account accountTo, double amount) throws TransactionLimitException, InsufficientBalanceException {
//    if (accountFrom.getAbsoluteLimit() < amount) {
//      if (accountTo == null || (accountTo.getAccountType() != AccountType.SAVINGS && accountFrom.getAccountType() != AccountType.SAVINGS)) {
//        throw new TransactionLimitException("Transaction limit exceeded");
//      }
//    }
//    if (accountFrom.getAccountBalance() < amount) {
//      if (accountTo == null || (accountTo.getAccountType() != AccountType.SAVINGS && accountFrom.getAccountType() != AccountType.SAVINGS)) {
//        throw new InsufficientBalanceException("Insufficient balance");
//      }
//    }
//  }
//  public void checkSameAccountTransfer(TransactionAccountDTO accountFrom, Account accountTo) {
//    if (Objects.equals(accountFrom.iban(), accountTo.getIban())) {
//      throw new IllegalArgumentException("Cannot transfer to the same account");
//    }
//  }
//  public void checkAccountStatus(Account account, String accountType){
//    if(!account.isActive()){
//      throw new IllegalArgumentException("Account is not active");
//    }
//  }

  public Transaction createTransactionFromDto(TransactionDTO transactionDTO, User initiator) {
    Transaction transaction = new Transaction();
    transaction.setAmount(transactionDTO.amount());
    transaction.setAccountFrom(accountRepository.findById(transactionDTO.accountFrom()).orElseThrow(() -> new EntityNotFoundException("Account with iban: " + transactionDTO.accountFrom() + " not found.")));
    transaction.setAccountTo(accountRepository.findById(transactionDTO.accountTo()).orElseThrow(() -> new EntityNotFoundException("Account with iban: " + transactionDTO.accountTo() + " not found.")));
    transaction.setDate(LocalDate.now());
    transaction.setTimestamp(LocalTime.now());
    transaction.setUserPerforming(initiator);
    return transaction;
  }

  public TransactionResponseDTO createDto(Transaction transaction) {
    TransactionAccountDTO accountFromDTO = new TransactionAccountDTO(transaction.getAccountFrom().getIban(),
        transaction.getAccountFrom().getAccountType(),
        transaction.getAccountFrom().getCustomer().getFullName());
    TransactionAccountDTO accountToDTO = new TransactionAccountDTO(transaction.getAccountTo().getIban(),
        transaction.getAccountTo().getAccountType(),
        transaction.getAccountTo().getCustomer().getFullName());
    return new TransactionResponseDTO(transaction.getTransactionID(), transaction.getAmount(), accountFromDTO, accountToDTO, transaction.getDate(), transaction.getTimestamp(), transaction.getUserPerforming().getFullName());
  }
  public double getSumOfMoneyTransferredToday(String email){
    Double amount = transactionRepository.getSumOfMoneyTransferredToday(email);
    return amount == null ? 0.00: amount;
  }
  public User createDummyUserForTransactions() {
    User user = new User();
    user.setFullName("DawoodIkram");
    userRepository.save(user);
    return user;
  }
}
