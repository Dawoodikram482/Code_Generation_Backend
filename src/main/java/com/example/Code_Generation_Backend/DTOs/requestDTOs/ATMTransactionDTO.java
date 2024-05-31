package com.example.Code_Generation_Backend.DTOs.requestDTOs;

import com.example.Code_Generation_Backend.models.CurrencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.NumberFormat;

public record ATMTransactionDTO(@NumberFormat @Positive Double amount, CurrencyType currencyType, @NotBlank String IBAN) {
}