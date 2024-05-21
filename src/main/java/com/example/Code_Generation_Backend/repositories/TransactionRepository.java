package com.example.Code_Generation_Backend.repositories;

import com.example.Code_Generation_Backend.models.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long>,
    JpaSpecificationExecutor<Transaction> {

  @Query("SELECT t FROM Transaction t WHERE t.accountFrom.iban = :iban")
  Page<Transaction> getTransactionsByCustomer(Pageable pageable, String iban);
  @Modifying
  @Transactional
  @Query("UPDATE Account a SET a.accountBalance = a.accountBalance- :amount WHERE a.iban = :accountFrom")
  void decreaseBalanceByAmount(double amount, String accountFrom);
  @Modifying
  @Transactional
  @Query("UPDATE Account a SET a.accountBalance = a.accountBalance+ :amount WHERE a.iban = :accountTo")
  void increaseBalanceByAmount(double amount, String accountTo);
  @Query("SELECT sum (t.amount) FROM Transaction t WHERE t.accountFrom.customer.email = :userEmail AND t.date = current_date AND t.accountFrom.accountType = com.example.Code_Generation_Backend.models.AccountType.CURRENT AND t.accountTo.accountType = com.example.Code_Generation_Backend.models.AccountType.CURRENT")
  Double getSumOfMoneyTransferredToday(@Param("iban") String iban);
}
