package com.example.Code_Generation_Backend.models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_CUSTOMER,
    ROLE_EMPLOYEE,
    ROLE_NEW;

    @Override
    public String getAuthority() {
        return name();
    }
}
