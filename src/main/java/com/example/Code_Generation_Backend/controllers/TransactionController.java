package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.ATMTransactionDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponseDTO;
import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.TransactionType;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.services.AccountService;
import com.example.Code_Generation_Backend.services.TransactionService;
import com.example.Code_Generation_Backend.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Predicate;

import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_LIMIT_STRING;
import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_OFFSET_STRING;


@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/transactions")
public class TransactionController {
  private final TransactionService transactionService;
  private final AccountService accountService;
  private final UserService userService;
  private final Predicate<GrantedAuthority> isEmployee =
      a -> a.getAuthority().equals(Role.ROLE_EMPLOYEE.name());

  public TransactionController(TransactionService transactionService, AccountService accountService, UserService userService) {
    this.transactionService = transactionService;
    this.accountService = accountService;
    this.userService = userService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_CUSTOMER')")
  public ResponseEntity<Object> getAllTransactions(
      @RequestParam(defaultValue = DEFAULT_LIMIT_STRING, required = false)
      int limit,
      @RequestParam(defaultValue = DEFAULT_OFFSET_STRING, required = false)
      int offset,
      @RequestParam(required = false) String ibanFrom,
      @RequestParam(required = false) String ibanTo,
      @RequestParam(required = false) Double amountMax,
      @RequestParam(required = false) Double amountMin,
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
      @RequestParam(required = false) TransactionType type,
      @AuthenticationPrincipal UserDetails jwtUser) {

    try {
      List<TransactionResponseDTO> transactions = transactionService.getAllTransactions(getPagination(limit, offset), id, ibanFrom, ibanTo, amountMin, amountMax, dateFrom, dateTo, type, jwtUser.getUsername());
      return ResponseEntity.status(HttpStatus.OK).body(transactions);
    } catch (Exception e) {
      if (e instanceof EntityNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      }

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping("/account/{iban}")
  @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
  public ResponseEntity<Object> getTransactionsByAccount(@RequestParam(defaultValue = DEFAULT_LIMIT_STRING, required = false)
                                                         int limit,
                                                         @RequestParam(defaultValue = DEFAULT_OFFSET_STRING, required = false)
                                                         int offset,
                                                         @PathVariable
                                                         String iban,
                                                         @AuthenticationPrincipal UserDetails jwtUser) {

    if (!transactionService.accountBelongsToUser(iban, jwtUser.getUsername()) && jwtUser.getAuthorities().stream().noneMatch(isEmployee)) {
      throw new BadCredentialsException("You are not authorized to perform this action");
    }
    try {
      return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactions(getPagination(limit, offset), iban));
    } catch (Exception e) {
      if (e instanceof BadCredentialsException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      if (e instanceof AuthenticationException) {
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
    } catch (Exception e) {
      if (e instanceof BadCredentialsException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      if (e instanceof AuthenticationException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    return null;
  }

  @PostMapping("/atm/deposit")
  @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
  public ResponseEntity<Object> deposit(@RequestBody ATMTransactionDTO atmTransactionDTO, @AuthenticationPrincipal UserDetails jwtUser) {
    try {
      return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.Deposit(atmTransactionDTO, jwtUser.getUsername()));
    } catch (Exception e) {
      if (e instanceof BadCredentialsException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      if (e instanceof AuthenticationException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PostMapping("/atm/withdraw")
  @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
  public ResponseEntity<Object> withdraw(@RequestBody ATMTransactionDTO atmTransactionDTO, @AuthenticationPrincipal UserDetails jwtUser) {
    try {
      return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.withdraw(atmTransactionDTO, jwtUser.getUsername()));
    } catch (Exception e) {
      if (e instanceof BadCredentialsException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      if (e instanceof AuthenticationException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping("/atm/withdraw")
  public ResponseEntity<Object> getWithdrawals() {
    try {
      return ResponseEntity.status(HttpStatus.OK).body(transactionService.getWithdrawals());
    } catch (Exception e) {
      if (e instanceof BadCredentialsException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      if (e instanceof AuthenticationException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping("/atm/deposit")
  public ResponseEntity<Object> getDeposits() {
    try {
      return ResponseEntity.status(HttpStatus.OK).body(transactionService.getDeposits());
    } catch (Exception e) {
      if (e instanceof BadCredentialsException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      if (e instanceof AuthenticationException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  public Pageable getPagination(int limit, int offset) {
    return PageRequest.of(offset / limit, limit);
  }

}
