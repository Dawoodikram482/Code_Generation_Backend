package com.example.Code_Generation_Backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  @Getter
  @Setter
  @Id
  @GeneratedValue(generator = "IBANGenerator", strategy = GenerationType.IDENTITY)
  @GenericGenerator(name = "IBANGenerator", strategy = "com.example.Code_Generation_Backend.generators.IBANGenerator")
  private String iban;

  private double accountBalance;
  @Builder.Default
  private LocalDate creationDate = LocalDate.now();
  private double absoluteLimit;
  @Builder.Default
  private boolean isActive = true;
  @Enumerated(EnumType.ORDINAL)
  private AccountType accountType;
  @ManyToOne
  private User customer;

  public void setAccountBalance(double accountBalance) {
    if (accountBalance > 0) {
      this.accountBalance = accountBalance;
    } else {
      throw new IllegalArgumentException("Balance can not be negative");
    }
  }


}
