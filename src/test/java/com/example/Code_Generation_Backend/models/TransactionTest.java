package com.example.Code_Generation_Backend.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
  @Test
  void testConstructor() {
    // Create test data
    User user = new User();
    Account accountSender = new Account();
    Account accountReceiver = new Account();
    double amount = 100.0;
    CurrencyType currencyType = CurrencyType.EURO;
    TransactionType transactionType = TransactionType.DEPOSIT;
    LocalDate localDate = LocalDate.now();
    LocalTime localTime = LocalTime.now();


    // Create a transaction using the constructor
    Transaction transaction = new Transaction(amount, accountReceiver, accountSender, localDate, localTime, user, transactionType, currencyType);

    // Verify the properties of the created transaction
    assertNotNull(transaction);
    assertEquals(user, transaction.getUserPerforming());
    assertEquals(accountSender, transaction.getAccountTo());
    assertEquals(accountReceiver, transaction.getAccountFrom());
    assertEquals(amount, transaction.getAmount());
    assertEquals(currencyType, transaction.getCurrencyType());
    assertEquals(transactionType, transaction.getTransactionType());
    assertNotNull(transaction.getTimestamp());
    assertNotNull(transaction.getDate());

  }

  @Test
  void testAmountNonZero_throwException() {
    Transaction transaction = new Transaction();
    assertThrows(IllegalArgumentException.class, () -> transaction.setAmount(0.0));
  }

  @Test
  void testAmountNonNegative_throwException() {
    Transaction transaction = new Transaction();
    assertThrows(IllegalArgumentException.class, () -> transaction.setAmount(-1.0));
  }
  @Test
  void userNotNull(){
    User user = new User();
    Transaction transaction = new Transaction(100.0, new Account(), new Account(), LocalDate.now(), LocalTime.now(), user, TransactionType.DEPOSIT, CurrencyType.EURO);
    assertNotNull(transaction.getUserPerforming());
  }

}