package com.example.Code_Generation_Backend.DTOs.responseDTOs;

import java.time.LocalDate;
import java.time.LocalTime;

public record TransactionResponeDTO(Long id, double amount, TransactionAccountDTO accountFrom,
                                    TransactionAccountDTO accountTo, LocalDate date, LocalTime time, String initiator) {
}
