package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.ATMTransactionDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionAccountDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponseDTO;
import com.example.Code_Generation_Backend.models.*;
import com.example.Code_Generation_Backend.models.exceptions.DailyLimitException;
import com.example.Code_Generation_Backend.models.exceptions.InsufficientBalanceException;
import com.example.Code_Generation_Backend.repositories.AccountRepository;
import com.example.Code_Generation_Backend.repositories.TransactionRepository;
import com.example.Code_Generation_Backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

  public List<TransactionResponseDTO> getAllTransactions(Pageable pageable, Long id,String ibanFrom, String ibanTo, Double amountMin, Double amountMax, LocalDate dateBefore, LocalTime timestamp, TransactionType type) {
    List<Transaction> transactions = transactionRepository.getTransactions(pageable, id, ibanFrom, ibanTo, amountMin, amountMax, dateBefore, timestamp, type).getContent();
    if (transactions.isEmpty()) {
      throw new EntityNotFoundException("No transactions found for account with iban: " + ibanFrom);
    }
    List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();
    transactions.forEach(transaction -> transactionResponseDTOS.add(createDto(transaction)));
    return transactionResponseDTOS;
  }

  public TransactionResponseDTO addTransaction(TransactionDTO transactionDTO, User initiator) {
    Transaction newTransaction = transactionRepository.save(createTransactionFromDto(transactionDTO, initiator, TransactionType.TRANSFER));
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


  public Transaction Deposit(ATMTransactionDTO atmTransaction, String userPerforming) throws AccountNotFoundException {
    Account receiver = accountService.getAccountByIBAN(atmTransaction.IBAN());
    if (receiver == null) {
      throw new AccountNotFoundException("Account with IBAN: " + atmTransaction.IBAN() + " not found");
    }
    User user = userService.getUserByEmail(userPerforming);
    checkAccountPreconditionsForWithdrawOrDeposit(receiver, user);
    Transaction transaction = new Transaction(
        atmTransaction.amount(),
        receiver,
        null,
        LocalDate.now(),
        LocalTime.now(),
        user,
        TransactionType.DEPOSIT,
        atmTransaction.currencyType()
    );
    updateAccountBalance(receiver, atmTransaction.amount(), true);
    return transactionRepository.save(transaction);
  }
  public Transaction withdraw(ATMTransactionDTO atmTransactionDTO, String userPerforming) throws AccountNotFoundException {
    Account withDrawer = accountService.getAccountByIBAN(atmTransactionDTO.IBAN());
    if (withDrawer == null) {
      throw new AccountNotFoundException("Account with IBAN: " + atmTransactionDTO.IBAN() + " not found");
    }
    User user = userService.getUserByEmail(userPerforming);
    checkAccountPreconditionsForWithdrawOrDeposit(withDrawer, user);
    Transaction transaction = new Transaction(
        atmTransactionDTO.amount(),
        null,
        withDrawer,
        LocalDate.now(),
        LocalTime.now(),
        user,
        TransactionType.WITHDRAWAL,
        atmTransactionDTO.currencyType()
    );
    updateAccountBalance(withDrawer, atmTransactionDTO.amount(), false);
    return transactionRepository.save(transaction);
  }

  boolean isUserAuthorizedToAccessAccount(User user, Account account) {
    return ((user == account.getCustomer() || user.getRoles().contains(Role.ROLE_EMPLOYEE)) && account.isActive());
  }

  private void checkAccountPreconditionsForWithdrawOrDeposit(Account account, User user) throws IllegalArgumentException {

    if (!isUserAuthorizedToAccessAccount(user, account)) {
      throw new IllegalArgumentException("You are not the owner of this account");
    }

    if (!account.isActive()) {
      throw new IllegalArgumentException("You cannot deposit/withdraw money to an inactive account");
    }

    if (account.getAccountType() == AccountType.SAVINGS) {
      throw new IllegalArgumentException("You cannot deposit/withdraw money to a savings account");
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

    if (accountFrom.getCustomer().getTransactionLimit() < amount) {
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

  public List<TransactionResponseDTO> getWithdrawals() {
    List<Transaction> transactions = transactionRepository.getTransactionsByType(TransactionType.WITHDRAWAL);
    if (transactions.isEmpty()) {
      throw new EntityNotFoundException("No withdrawals found");
    }
    List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();
    transactions.forEach(transaction -> transactionResponseDTOS.add(createDto(transaction)));
    return transactionResponseDTOS;
  }

  public List<TransactionResponseDTO> getDeposits() {
    List<Transaction> transactions = transactionRepository.getTransactionsByType(TransactionType.DEPOSIT);
    if (transactions.isEmpty()) {
      throw new EntityNotFoundException("No deposits found");
    }
    List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();
    transactions.forEach(transaction -> transactionResponseDTOS.add(createDto(transaction)));
    return transactionResponseDTOS;
  }

  public Transaction createTransactionFromDto(TransactionDTO transactionDTO, User initiator, TransactionType transactionType) {
    Transaction transaction = new Transaction();
    transaction.setAmount(transactionDTO.amount());
    transaction.setAccountFrom(accountRepository.findById(transactionDTO.accountFrom()).orElseThrow(() -> new EntityNotFoundException("Account with iban: " + transactionDTO.accountFrom() + " not found.")));
    transaction.setAccountTo(accountRepository.findById(transactionDTO.accountTo()).orElseThrow(() -> new EntityNotFoundException("Account with iban: " + transactionDTO.accountTo() + " not found.")));
    transaction.setDate(LocalDate.now());
    transaction.setTimestamp(LocalTime.now().withNano(0));
    transaction.setUserPerforming(initiator);
    transaction.setTransactionType(transactionType);
    transaction.setCurrencyType(CurrencyType.EURO);
    return transaction;
  }

  public TransactionResponseDTO createDto(Transaction transaction) {
    TransactionAccountDTO accountFromDTO = new TransactionAccountDTO(transaction.getAccountFrom().getIban(),
        transaction.getAccountFrom().getAccountType(),
        transaction.getAccountFrom().getCustomer().getFullName());
    TransactionAccountDTO accountToDTO = new TransactionAccountDTO(transaction.getAccountTo().getIban(),
        transaction.getAccountTo().getAccountType(),
        transaction.getAccountTo().getCustomer().getFullName());
    return new TransactionResponseDTO(transaction.getTransactionID(), transaction.getAmount(), accountFromDTO, accountToDTO, transaction.getDate(), transaction.getTimestamp(), transaction.getUserPerforming().getFullName(), transaction.getTransactionType());
  }

  public double getSumOfMoneyTransferredToday(String email) {
    Double amount = transactionRepository.getSumOfMoneyTransferredToday(email);
    return amount == null ? 0.00 : amount;
  }
}
