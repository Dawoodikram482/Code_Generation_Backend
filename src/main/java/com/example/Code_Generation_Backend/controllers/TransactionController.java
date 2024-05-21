package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.services.AccountService;
import com.example.Code_Generation_Backend.services.TransactionService;
import com.example.Code_Generation_Backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponseDTO;

import java.time.LocalDate;
import java.util.List;

import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_LIMIT_STRING;
import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_OFFSET_STRING;


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
  @GetMapping
  public ResponseEntity<Object>getAllTransactions(
          @RequestParam(defaultValue = DEFAULT_LIMIT_STRING, required = false)
                  int limit,
          @RequestParam(defaultValue = DEFAULT_OFFSET_STRING, required = false)
                  int offset,
          @RequestParam(required = false) String ibanFrom,
          @RequestParam(required = false) String ibanTo,
          @RequestParam(required = false) Double amountMax,
          @RequestParam(required = false) Double amountMin,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo)
  {
    List<TransactionResponseDTO> transactions = transactionService.getAllTransactions(getPagination(limit, offset), ibanFrom, ibanTo, amountMin, amountMax, dateFrom, dateTo);
    return ResponseEntity.ok().body(transactions);
  }
  @GetMapping("/account/{iban}")
  public ResponseEntity<Object> getTransactionsByAccount( @RequestParam(defaultValue = DEFAULT_LIMIT_STRING , required = false)
                                                            int limit,
                                                          @RequestParam(defaultValue = DEFAULT_OFFSET_STRING, required = false)
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
  private Pageable getPagination(int limit, int offset) {
    return PageRequest.of(offset / limit, limit);
  }
}
