package com.example.Code_Generation_Backend.DTOs.responseDTOs;

import com.example.Code_Generation_Backend.models.Role;

public record LoginResponseDTO(String email, String token, Role role, String firstName, String lastName) {
}
