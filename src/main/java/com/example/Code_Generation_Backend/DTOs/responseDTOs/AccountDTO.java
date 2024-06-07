package com.example.Code_Generation_Backend.DTOs.responseDTOs;
import com.example.Code_Generation_Backend.models.AccountType;
public record AccountDTO(String iban, AccountType accountType, boolean isActive, UserDTO customer, double accountBalance) {
}
