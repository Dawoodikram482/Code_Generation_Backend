package com.example.Code_Generation_Backend.DTOs.responseDTOs;

import com.example.Code_Generation_Backend.models.TransactionType;

import java.time.LocalDate;
import java.time.LocalTime;

public record TransactionResponseDTO(Long id, double amount, TransactionAccountDTO accountFrom,
                                     TransactionAccountDTO accountTo, LocalDate date, LocalTime time, String initiator, TransactionType transactionType) {
}
