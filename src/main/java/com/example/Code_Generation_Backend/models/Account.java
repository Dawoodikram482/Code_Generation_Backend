package com.example.Code_Generation_Backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

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

  @Enumerated(EnumType.STRING)
  private AccountType accountType;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User customer;

  public void setAccountBalance(double accountBalance) {
    if (accountBalance >= 0) {
      this.accountBalance = accountBalance;
    } else {
      throw new IllegalArgumentException("Balance cannot be negative");
    }
  }

  public void setAbsoluteLimit(double absoluteLimit) {
    if (absoluteLimit >= 0) {
      this.absoluteLimit = absoluteLimit;
    } else {
      throw new IllegalArgumentException("Absolute limit cannot be negative");
    }
  }

  // Method to set account type
  public void setAccountType(AccountType accountType) {
    this.accountType = accountType;
  }

  public void setCustomer(User customer) {
    this.customer = customer;
  }
}
