package com.example.Code_Generation_Backend.DTOs.requestDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDTO (
      @NotBlank(message = "BSN is required") String bsn,
      @NotBlank(message = "Email is required")
      @Email(message = "Email is invalid") String email,
      @NotBlank(message = "Password is required") String password,
      @NotBlank(message = "First Name is required") String firstName,
      @NotBlank(message = "Last Name is required") String lastName,
      @NotBlank(message = "Phone Number is required") String phoneNumber,
      @NotBlank(message = "Date of Birth is required") String dateOfBirth
) {
}