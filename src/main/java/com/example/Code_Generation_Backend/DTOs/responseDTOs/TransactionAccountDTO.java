package com.example.Code_Generation_Backend.DTOs.responseDTOs;

import com.example.Code_Generation_Backend.models.AccountType;

public record TransactionAccountDTO(String iban, AccountType accountType, String user){
}
