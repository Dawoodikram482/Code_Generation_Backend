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
@Entity(name="users")
public class User {

    @Id
    @GeneratedValue
    private long id;
    @Column(unique = true)
    private String bsn;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
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

    public void setDayLimit(double dayLimit){
        if(dayLimit>0){
            this.dayLimit =dayLimit;
        }else{
            this.dayLimit = 0;
        }
    }
    public void setTransactionLimit(double transactionLimit){
        if(transactionLimit>0){
            this.transactionLimit = transactionLimit;
        }else{
            this.transactionLimit = 0;
        }
    }
    public String getFullName(){return firstName+""+lastName;}
    public void addRole(Role role){
        if(roles.contains(role)){
            return;
        }
        roles.add(role);
    }

    public Role getRole() {
        if (roles != null && !roles.isEmpty()) {
            return roles.getFirst();
        }
        return null; // or return a default role
    }

    public void setRole(Role newRole) {
    public void setFullName(String DawoodIkram) {

    }
}
