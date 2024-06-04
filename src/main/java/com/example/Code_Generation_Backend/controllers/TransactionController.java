package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.ATMTransactionDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponseDTO;
import com.example.Code_Generation_Backend.models.TransactionType;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.services.AccountService;
import com.example.Code_Generation_Backend.services.TransactionService;
import com.example.Code_Generation_Backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_LIMIT_STRING;
import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_OFFSET_STRING;


@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/transactions")
public class TransactionController {
  private final TransactionService transactionService;
  private final AccountService accountService;
  private final UserService userService;

  public TransactionController(TransactionService transactionService, AccountService accountService, UserService userService) {
    this.transactionService = transactionService;
    this.accountService = accountService;
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<Object> getAllTransactions(
      @RequestParam(defaultValue = DEFAULT_LIMIT_STRING, required = false)
      int limit,
      @RequestParam(defaultValue = DEFAULT_OFFSET_STRING, required = false)
      int offset,
      @RequestParam(required = false) String ibanFrom,
      @RequestParam(required = false) String ibanTo,
      @RequestParam(required = false) Double amountMax,
      @RequestParam(required = false) Double amountMin,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime timestamp,
      @RequestParam(required = false) TransactionType type) {
    List<TransactionResponseDTO> transactions = transactionService.getAllTransactions(getPagination(limit, offset), ibanFrom, ibanTo, amountMin, amountMax, dateFrom, timestamp, type);
    return ResponseEntity.ok().body(transactions);
  }

  @GetMapping("/account/{iban}")
  @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
  public ResponseEntity<Object> getTransactionsByAccount(@RequestParam(defaultValue = DEFAULT_LIMIT_STRING, required = false)
                                                         int limit,
                                                         @RequestParam(defaultValue = DEFAULT_OFFSET_STRING, required = false)
                                                         int offset,
                                                         @PathVariable
                                                         String iban) {
//    List<TransactionResponseDTO> transactions = transactionService.getTransactions(getPagination(limit, offset), iban);
//    return ResponseEntity.ok().body(transactions);
    try{
      return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactions(getPagination(limit, offset), iban));
    }catch (Exception e){
      if(e instanceof BadCredentialsException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      if(e instanceof AuthenticationException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
  public ResponseEntity<Object> addTransaction(@RequestBody @Valid TransactionDTO transactionDTO, @AuthenticationPrincipal UserDetails jwtUser) {
    try {
      if (transactionService.isValidTransaction(transactionDTO)) {
        User userPerforming = userService.getUserByEmail(jwtUser.getUsername());
        transactionService.changeBalance(transactionDTO.amount(), transactionDTO.accountFrom(), transactionDTO.accountTo());
        TransactionResponseDTO newTransaction = transactionService.addTransaction(transactionDTO, userPerforming);
        return ResponseEntity.ok().body(newTransaction);
      }
    }catch (Exception e){
      if(e instanceof BadCredentialsException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      if(e instanceof AuthenticationException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    return null;
  }

 /* @PostMapping("/atm/deposit")
  @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
  public ResponseEntity<Object> deposit(@RequestBody ATMTransactionDTO atmTransactionDTO, @AuthenticationPrincipal UserDetails jwtUser) {
    try{
      return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.Deposit(atmTransactionDTO, jwtUser.getUsername()));
  } catch (Exception e) {
      if (e instanceof BadCredentialsException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      if(e instanceof AuthenticationException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }*/

  @GetMapping("/atm/withdraw")
  public ResponseEntity<Object> getWithdrawals() {
    try {
      return ResponseEntity.status(HttpStatus.OK).body(transactionService.getWithdrawals());
    } catch (Exception e) {
      if (e instanceof BadCredentialsException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      if(e instanceof AuthenticationException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
  @GetMapping("/atm/deposit")
  public ResponseEntity<Object>getDeposits(){
    try{
      return ResponseEntity.status(HttpStatus.OK).body(transactionService.getDeposits());
    } catch (Exception e) {
      if (e instanceof BadCredentialsException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      if(e instanceof AuthenticationException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
  private Pageable getPagination(int limit, int offset) {
    return PageRequest.of(offset / limit, limit);
  }

}
