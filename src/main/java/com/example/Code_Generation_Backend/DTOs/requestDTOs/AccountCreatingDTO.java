package com.example.Code_Generation_Backend.DTOs.requestDTOs;

import com.example.Code_Generation_Backend.CustomValidators.ValidAccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AccountCreatingDTO(
    @NotNull(message = "Day Limit cannot be left empty")
    @PositiveOrZero(message = "Day Limit cannot be negative")
    Double dayLimit,
    @NotNull(message = "Transaction Limit cannot be left empty")
    @PositiveOrZero(message = "Absolute Limit cannot be negative")
    Double absoluteLimit,
    @PositiveOrZero(message = "Transaction Limit cannot be negative")
    Double transactionLimit,
    @ValidAccountType
    String accountType,
    @NotNull(message = "accountHolderId cannot be left empty") Long accountHolderId)
 {
}
