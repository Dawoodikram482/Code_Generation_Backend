package com.example.Code_Generation_Backend.DTOs.requestDTOs;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CustomerRegistrationDTO {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String bsn;

    @NotNull
    private LocalDate birthDate;

 /*   @NotBlank
    private String accountType;*/

    @NotBlank
    private String password;

    // Getters and setters

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBsn(String bsn) {
        this.bsn = bsn;
    }


    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setPassword(String password) {
        this.password = password;
    }


/*    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }*/


}

