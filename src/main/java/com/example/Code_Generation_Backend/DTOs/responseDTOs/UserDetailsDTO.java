package com.example.Code_Generation_Backend.DTOs.responseDTOs;

import com.example.Code_Generation_Backend.models.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//this is what the client see in their account overview
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private String firstName;
    private String lastName;
    private String iban;
    private double accountBalance;
    private AccountType accountType;
}
