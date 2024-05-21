package com.example.Code_Generation_Backend.DTOs.responseDTOs;

import com.example.Code_Generation_Backend.models.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private String bsn;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;
    private String role;  // Representing Role as String

    public UserDTO(String bsn, String firstName, String lastName, LocalDate dateOfBirth, String phoneNumber, String email, Role role) {
        this.bsn = bsn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role.name(); // Convert Role enum to String
    }


    @Setter
    @Getter
    @NotNull
    private Long userId;

    @Setter
    @Getter
    @NotNull
    private String newRole;


}
