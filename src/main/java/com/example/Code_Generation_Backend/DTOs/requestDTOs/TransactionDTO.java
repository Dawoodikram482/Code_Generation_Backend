package com.example.Code_Generation_Backend.DTOs.requestDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.NumberFormat;

public record TransactionDTO(@NumberFormat @Positive Double amount, @NotBlank String accountTo, @NotBlank String accountFrom){
}
