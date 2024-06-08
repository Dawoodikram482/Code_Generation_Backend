package com.example.Code_Generation_Backend.repositories.Specifications;


import com.example.Code_Generation_Backend.models.Transaction;
import com.example.Code_Generation_Backend.models.TransactionType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public class TransactionSpecifications {

  public static Specification<Transaction> getTransactionsByFilters(
      Long id, String ibanFrom, String ibanTo, Double amountMin, Double amountMax, LocalDate dateBefore, LocalTime timestamp, TransactionType type) {

    return (root, query, criteriaBuilder) -> {
      Predicate predicate = criteriaBuilder.conjunction();

      if (id != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("id"), id));
      }
      if (ibanFrom != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("accountFrom").get("iban"), ibanFrom));
      }
      if (ibanTo != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("accountTo").get("iban"), ibanTo));
      }
      if (amountMin != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), amountMin));
      }
      if (amountMax != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("amount"), amountMax));
      }
      if (dateBefore != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateBefore));
      }
      if (timestamp != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("timestamp"), timestamp));
      }
      if (type != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("transactionType"), type));
      }

      return predicate;
    };
  }
}
