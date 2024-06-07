package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.AbsoluteLimitRequestDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.AccountDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionAccountDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDTO;
import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.AccountType;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.services.AccountService;
import com.example.Code_Generation_Backend.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.function.Function;

import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_LIMIT_STRING;
import static com.example.Code_Generation_Backend.models.Constants.DEFAULT_OFFSET_STRING;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
@RequestMapping("/accounts")
@ControllerAdvice
public class AccountController {

  private final String DEFAULT_OFFSET_STRING = "0";
  private final String DEFAULT_LIMIT_STRING = "50";
  private final AccountService accountService;
  private final UserService userService;

  public AccountController(AccountService accountService, UserService userService) {
    this.accountService = accountService;
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<Object> getAllAccounts(
      @RequestParam(defaultValue = DEFAULT_LIMIT_STRING, required = false) int limit,
      @RequestParam(defaultValue = DEFAULT_OFFSET_STRING, required = false) int offset,
      @RequestParam(required = false) String accountType) {
    AccountType passingAccountType = null;
    try {
      if (accountType != null) {
        passingAccountType = AccountType.valueOf(accountType.toUpperCase());
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "The account type is not valid"); // this will be caught by RestControllerExceptionHandler
    }
    List<Account> accounts = accountService.getAllAccounts(limit, offset, passingAccountType);
    return ResponseEntity.ok(
        accounts.parallelStream().map(mapAccountObjectToDTO).toList()
        // using Parallel Stream to improve performance
    );
  }

  ;

  @GetMapping("/search-iban")
  @PreAuthorize(value = "hasRole('ROLE_CUSTOMER')")
  public String searchIban(@RequestParam String firstName, @RequestParam String lastName) {
    try {
      return accountService.getIbanByName(firstName, lastName);
    } catch (AccountNotFoundException e) {
      return e.getMessage();
    }
  }

  @PostMapping("/closeAccount/{iban}")
  @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE')")
  public ResponseEntity<Object> closeAccount(@PathVariable String iban) {
    try {
      boolean isClosed = accountService.closeAccount(iban);
      if (isClosed) {
        return ResponseEntity.ok().build();
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to close the account");
      }
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
    }
  }
  @PutMapping("/{IBAN}/limit")
  @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE')")
  public ResponseEntity<Object> updateAbsoluteLimit(@PathVariable String IBAN, @RequestBody AbsoluteLimitRequestDTO absoluteLimitRequestDTO) {
    try {
      Account account = accountService.getAccountByIBAN(IBAN);
      return ResponseEntity.ok(accountService.updateAbsoluteLimit(account, absoluteLimitRequestDTO));
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
    catch (AccountNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
    }
  }

    private final Function<User, UserDTO> mapUserObjectToDTO = user ->
            new UserDTO(user.getId(), user.getBsn(), user.getFirstName(), user.getLastName(),
                    user.getDateOfBirth(), user.getPhoneNumber(), user.getEmail(), user.isActive(),
                    user.getDayLimit(), user.isApproved(), user.getTransactionLimit()
            );

    private final Function<Account, AccountDTO> mapAccountObjectToDTO = account ->
            new AccountDTO(account.getIban(), account.getAccountType(), account.isActive(), mapUserObjectToDTO.apply(account.getCustomer()), account.getAccountBalance());

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE')")
    public ResponseEntity<Object> getAccountByStatus(@RequestParam(defaultValue = DEFAULT_LIMIT_STRING, required = false)
                                                         int limit,
                                                     @RequestParam(defaultValue = DEFAULT_OFFSET_STRING, required = false)
                                                         int offset) {
        try {
            List<Account> accounts = accountService.getAccountByStatus(getPagination(limit,offset),true);
            return ResponseEntity.ok(accounts.parallelStream().map(mapAccountObjectToDTO).toList());
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    Pageable getPagination(int limit, int offset) {
        return PageRequest.of(offset / limit, limit);
    }
}

