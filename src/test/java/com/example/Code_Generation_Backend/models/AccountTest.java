package com.example.Code_Generation_Backend.models;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.TransactionDTO;
import com.example.Code_Generation_Backend.services.AccountService;
import com.example.Code_Generation_Backend.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AccountTest
{
    private User user;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;


    private Account activeAccount;

    private Account inactiveAccount;
    private TransactionDTO testTransactionDTO;
    @BeforeEach
    void setUp() {
        user = User.builder().build();

        MockitoAnnotations.openMocks(this);

        User user = new User();

        activeAccount = new Account("NL01RABO0300065264", 100.0, LocalDate.now(), 10.0, true, AccountType.CURRENT, user);
        inactiveAccount = new Account("NL01INHB0000000002", 1000.0, LocalDate.now(), 500, false, AccountType.CURRENT, user);

        testTransactionDTO = new TransactionDTO(20.0, "NL01RABO0300065264", "NL01INHB0000000002");
    }

    //check if the absolute limt can be negative
    @Test
    void testAbsoluteLimitPositive() {

        double positiveLimit = 100.0;
        activeAccount.setAbsoluteLimit(positiveLimit);
        assertEquals(positiveLimit, activeAccount.getAbsoluteLimit());
    }

    @Test
    void testAbsoluteLimitZero() {

        double zeroLimit = 0.0;
        activeAccount.setAbsoluteLimit(zeroLimit);
        assertEquals(zeroLimit, activeAccount.getAbsoluteLimit());
    }

    @Test
    void testAbsoluteLimitNegative() {

        double zeroLimit = -100;
        assertThrows(IllegalArgumentException.class, () -> activeAccount.setAbsoluteLimit(-100.0), "Absolute limit cannot be negative");
    }

    @Test
    void testBalanceCannotBeNegative() {

        assertThrows(IllegalArgumentException.class, () -> activeAccount.setAbsoluteLimit(-100.0), "balance cannot be negative");
    }

    @Test
    void testTransactionFromAndToInactiveAccount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.validateAccountAndTransferConditions(testTransactionDTO, activeAccount, inactiveAccount)
        );
        assertTrue(exception.getMessage().contains("You cannot transfer to or from an inactive account"));
    }
}
