package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionAccountDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponseDTO;
import com.example.Code_Generation_Backend.models.Transaction;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.repositories.AccountRepository;
import com.example.Code_Generation_Backend.repositories.TransactionRepository;
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

  public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, UserService userService) {
    this.transactionRepository = transactionRepository;
    this.accountRepository = accountRepository;
    this.userService = userService;
  }
  public Transaction createTransactionFromDto(TransactionDTO transactionDTO, User initiator){
    Transaction transaction = new Transaction();
    transaction.setAmount(transactionDTO.amount());
    transaction.setAccountFrom(accountRepository.findById(transactionDTO.accountFrom()).get());
    transaction.setAccountTo(accountRepository.findById(transactionDTO.accountTo()).get());
    transaction.setDate(LocalDate.now());
    transaction.setTimestamp(LocalTime.now());
    transaction.setUserPerforming(initiator);
    return transaction;
  }
  public TransactionResponseDTO createDto(Transaction transaction){
    TransactionAccountDTO accountFromDTO = new TransactionAccountDTO(transaction.getAccountFrom().getIban(), transaction.getAccountFrom().getAccountType(), transaction.getAccountFrom().getCustomer().getFullName());
    TransactionAccountDTO accountToDTO = new TransactionAccountDTO(transaction.getAccountTo().getIban(), transaction.getAccountTo().getAccountType(), transaction.getAccountTo().getCustomer().getFullName());
    return new TransactionResponseDTO(transaction.getTransactionID(), transaction.getAmount(), accountFromDTO, accountToDTO, transaction.getDate(), transaction.getTimestamp(), transaction.getUserPerforming().getFullName());
  }
  public void addTransaction(TransactionDTO transactionDTO, User initiator){
    Transaction newTransaction = transactionRepository.save(createTransactionFromDto(transactionDTO, initiator));
    createDto(newTransaction);
  }
  public List<TransactionResponseDTO> getTransactions(Pageable pageable, String iban){
    List<Transaction> transactions = transactionRepository.getTransactionsByCustomer(pageable,iban).getContent();
    if(transactions.isEmpty()){
      throw new EntityNotFoundException("No transactions found for account with iban: " + iban);
    }
    List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();
    transactions.forEach(transaction -> transactionResponseDTOS.add(createDto(transaction)));
    return transactionResponseDTOS;
  }
}
