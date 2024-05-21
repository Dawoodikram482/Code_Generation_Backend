package com.example.Code_Generation_Backend.repositories;

import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {
  Page<Account> findAccountByAccountTypeEqualsAndIbanNot(Pageable pageable, AccountType accountType, String iban);
  Page<Account> findByAndIbanNot(Pageable pageable, String iban);
  List<Account> findByCustomer_IdEquals(long id);
  int countAccountByCustomer_IdEqualsAndAccountTypeEquals(long customerId, AccountType accountType);
  boolean existsAccountByCustomerEmailEqualsIgnoreCaseAndIbanEquals(String iban, String email);
  Optional<Account> findByIban(String iban);
}
