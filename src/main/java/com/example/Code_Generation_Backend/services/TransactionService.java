package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionAccountDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponseDTO;
import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.Transaction;
import com.example.Code_Generation_Backend.models.User;
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
  public void updateAccountBalance(Account account, double amount, boolean isDeposit){
    double balance = account.getAccountBalance();
    if(isDeposit){
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

  public List<TransactionResponseDTO> getTransactions(Pageable pageable, String iban) {
    List<Transaction> transactions = transactionRepository.getTransactionsByCustomer(pageable, iban).getContent();
    if (transactions.isEmpty()) {
      throw new EntityNotFoundException("No transactions found for account with iban: " + iban);
    }
    List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();
    transactions.forEach(transaction -> transactionResponseDTOS.add(createDto(transaction)));
    return transactionResponseDTOS;
  }
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
  public User createDummyUserForTransactions() {
    User user = new User();
    user.setFullName("DawoodIkram");
    userRepository.save(user);
    return user;
  }
}
