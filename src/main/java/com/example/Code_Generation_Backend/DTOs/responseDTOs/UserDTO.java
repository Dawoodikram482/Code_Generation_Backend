package com.example.Code_Generation_Backend.DTOs.responseDTOs;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UserDTO(
    @NotNull(message = "User Id cannot be left empty!") Long id,
    String bsn,
    @NotNull(message = "First Name cannot be left empty") String firstName,
    @NotNull(message = "Last Name cannot be left empty") String lastName,
    LocalDate dateOfBirth,
    String phoneNumber,
    String email,
    boolean isActive,
    double dayLimit,
    boolean isApproved,
    double transactionLimit
    ) {

}
