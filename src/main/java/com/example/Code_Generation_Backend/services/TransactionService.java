package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionAccountDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponeDTO;
import com.example.Code_Generation_Backend.models.Transaction;
import com.example.Code_Generation_Backend.repositories.AccountRepository;
import com.example.Code_Generation_Backend.repositories.TransactionRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
}
