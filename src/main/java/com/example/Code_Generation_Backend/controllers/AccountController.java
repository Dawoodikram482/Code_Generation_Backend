package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.responseDTOs.AccountDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDTO;
import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.AccountType;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.services.AccountService;
import com.example.Code_Generation_Backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.function.Function;

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
    };


    @GetMapping("/search-iban")
    @PreAuthorize(value = "hasRole('ROLE_CUSTOMER')")
    public String searchIban(@RequestParam String firstName, @RequestParam String lastName) {
        try {
            return accountService.getIbanByName(firstName, lastName);
        } catch (AccountNotFoundException e) {
            return e.getMessage();
        }
    }

    private final Function<User, UserDTO> mapUserObjectToDTO = user ->
            new UserDTO(user.getId(), user.getBsn(), user.getFirstName(), user.getLastName(),
                    user.getDateOfBirth(), user.getPhoneNumber(), user.getEmail(), user.isActive(),
                    user.getDayLimit(), user.isApproved(), user.getTransactionLimit()
            );

//    private final Function<Account, TransactionAccountDTO> mapAccountObjectToDTO = account ->
//            new TransactionAccountDTO(account.getIban(), account.getAccountType(), account.getCustomer().getFullName());

    private final Function<Account, AccountDTO> mapAccountObjectToDTO = account ->
            new AccountDTO(account.getIban(), account.getAccountType(), mapUserObjectToDTO.apply(account.getCustomer()));
}








    /*private final Function<Account, TransactionAccountDTO> mapAccountObjectToDTO = account -> new TransactionAccountDTO(account.getIban(), account.getAccountType(), account.getCustomer());
}*/