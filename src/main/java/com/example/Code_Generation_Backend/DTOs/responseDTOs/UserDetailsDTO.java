package com.example.Code_Generation_Backend.DTOs.responseDTOs;

import com.example.Code_Generation_Backend.models.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String bsn;
    private List<AccountDTO> accounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountDTO {
        private String iban;
        private double accountBalance;
        private AccountType accountType;
    }
}
