package com.example.Code_Generation_Backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String bsn;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;

    private Boolean isApproved;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    private boolean isActive;

    @Builder.Default
    private double dayLimit = 500;

    @Builder.Default
    private double transactionLimit = 200;

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;

    // Ensure the dayLimit is always positive
    public void setDayLimit(double dayLimit) {
        this.dayLimit = Math.max(dayLimit, 0);
    }

    // Ensure the transactionLimit is always positive
    public void setTransactionLimit(double transactionLimit) {
        this.transactionLimit = Math.max(transactionLimit, 0);
    }

    // Get the full name by combining first and last name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Add a role to the user if it doesn't already exist
    public void addRole(Role role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    // Get the primary role of the user
    public Role getRole() {
        if (roles != null && !roles.isEmpty()) {
            return roles.get(0);
        }
        return null; // or return a default role
    }

    // Check if the user is approved
    public boolean isApproved() {
        return isApproved;
    }

    // Set the approved status of the user
    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // Placeholder methods (if needed)
    public void setRole(Role newRole) {
        // Implement if required
    }


}
