package com.example.Code_Generation_Backend.repositories;

import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {
  Page<Account> findAccountByAccountTypeEqualsAndIbanNot(@NonNull Pageable pageable, @NonNull AccountType accountType, @NonNull String iban);
  Page<Account> findByAndIbanNot(@NonNull Pageable pageable, @NonNull String iban);
  List<Account> findByCustomer_IdEquals(long id);
  int countAccountByCustomer_IdEqualsAndAccountTypeEquals(long customerId, @NonNull AccountType accountType);
  Page<Account> findByAccountType(@NonNull AccountType accountType, @NonNull Pageable pageable);
  @Query("SELECT a FROM Account a")
  Page<Account> findAll(@NonNull Pageable pageable);
  boolean existsAccountByCustomerEmailEqualsIgnoreCaseAndIbanEquals(String iban, String email);
  Optional<Account> findByIban(String iban);
  Optional<Account> findByCustomerFirstNameAndCustomerLastName(String firstName, String lastName);
  boolean existsByIban(String iban);
}
