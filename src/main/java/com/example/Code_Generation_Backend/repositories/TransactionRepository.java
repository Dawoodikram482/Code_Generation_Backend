package com.example.Code_Generation_Backend.repositories;

import com.example.Code_Generation_Backend.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long>,
    JpaSpecificationExecutor<Transaction> {

  @Query("SELECT t FROM Transaction t WHERE t.accountFrom.iban = :iban")
  Page<Transaction> getTransactionsByCustomer(Pageable pageable, String iban);
}
