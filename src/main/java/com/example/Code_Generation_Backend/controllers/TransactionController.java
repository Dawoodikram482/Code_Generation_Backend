package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.ATMTransactionDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.services.AccountService;
import com.example.Code_Generation_Backend.services.TransactionService;
import com.example.Code_Generation_Backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponseDTO;

import java.util.List;


@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/transactions")
public class TransactionController {
  private TransactionService transactionService;
  private AccountService accountService;
  private UserService userService;

  public TransactionController(TransactionService transactionService, AccountService accountService, UserService userService) {
    this.transactionService = transactionService;
    this.accountService = accountService;
    this.userService = userService;
  }

  @GetMapping("/account/{iban}")
  public ResponseEntity<Object> getTransactionsByAccount( @RequestParam(defaultValue = "50", required = false)
                                                            int limit,
                                                          @RequestParam(defaultValue = "0", required = false)
                                                            int offset,
                                                          @PathVariable
                                                            String iban)
  {
    List <TransactionResponseDTO> transactions = transactionService.getTransactions(getPagination(limit, offset), iban);
    return ResponseEntity.ok().body(transactions);
  }
  @PostMapping
  public ResponseEntity<Object> addTransaction(@RequestBody @Valid TransactionDTO transactionDTO) {
    transactionService.processTransaction(transactionDTO);
    return ResponseEntity.ok().body("Transaction added successfully");
  }

  @PostMapping("/atm")
  public ResponseEntity<Object> deposit(@RequestBody ATMTransactionDTO atmTransactionDTO) {
    transactionService.processATMTransaction(atmTransactionDTO);
    return ResponseEntity.ok().body("Transaction added successfully");
  }

  private Pageable getPagination(int limit, int offset) {
    return PageRequest.of(offset / limit, limit);
  }
}
