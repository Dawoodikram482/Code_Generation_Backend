package com.example.Code_Generation_Backend.repositories.Specifications;


import com.example.Code_Generation_Backend.models.Transaction;
import com.example.Code_Generation_Backend.models.TransactionType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TransactionSpecifications {

  public static Specification<Transaction> getTransactionsByFilters(
          Long id, List<String> ibansFrom, List<String> ibansTo, Double amountMin, Double amountMax, LocalDate dateFrom, LocalDate dateTo, TransactionType type) {

    return (root, query, criteriaBuilder) -> {
      Predicate predicate = criteriaBuilder.conjunction();

      if (id != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("id"), id));
      }

      // Handling ibansFrom and ibansTo with an OR condition
      if ((ibansFrom != null && !ibansFrom.isEmpty()) || (ibansTo != null && !ibansTo.isEmpty())) {
        Predicate ibanPredicate = criteriaBuilder.disjunction();

        if (ibansFrom != null && !ibansFrom.isEmpty()) {
          ibanPredicate = criteriaBuilder.or(ibanPredicate, root.get("accountFrom").get("iban").in(ibansFrom));
        }
        if (ibansTo != null && !ibansTo.isEmpty()) {
          ibanPredicate = criteriaBuilder.or(ibanPredicate, root.get("accountTo").get("iban").in(ibansTo));
        }

        predicate = criteriaBuilder.and(predicate, ibanPredicate);
      }

      if (amountMin != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), amountMin));
      }
      if (amountMax != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("amount"), amountMax));
      }
      if (dateFrom != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateFrom));
      }
      if (dateTo != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateTo));
      }
      if (type != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("transactionType"), type));
      }

      return predicate;
    };
  }
}
