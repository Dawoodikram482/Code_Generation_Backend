package com.example.Code_Generation_Backend.DTOs.responseDTOs;

import com.example.Code_Generation_Backend.models.AccountType;
import com.example.Code_Generation_Backend.models.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionAccountDTO {
    // Optionally, you can add setters if needed
    // Getters
    private String iban;
    private AccountType accountType;
    private User customer;

    public TransactionAccountDTO(String iban, AccountType accountType, User customer) {
        this.iban = iban;
        this.accountType = accountType;
        this.customer = customer;
    }

}
