package com.example.Code_Generation_Backend.config;

import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
@Component
public class RuntimeDataSeeder implements ApplicationRunner {

    private final UserService userService;
    public RuntimeDataSeeder(UserService userService){
        this.userService=userService;
    }
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        seedEmployee();
        User employeeCustomer = seedEmployeeCustomer();
        User customer = seedCustomer();
    }
    private void seedEmployee(){
        User seedEmployee = User.builder()
                .bsn("123456789")
                .firstName("Dipika")
                .lastName("Bhandari")
                .dateOfBirth(LocalDate.of(2003,5,8))
                .phoneNumber("9987654123")
                .email("db@gmail.com")
                .password("password")
                .isActive(true)
                .roles(List.of(Role.ROLE_EMPLOYEE))
                .build();
        userService.SaveUser(seedEmployee);
    }
    private User seedCustomer(){
        User seedCustomer = User.builder()
                .bsn("509547989")
                .firstName("Dawood")
                .lastName("Ikram")
                .dateOfBirth(LocalDate.of(2003, 7, 16))
                .phoneNumber("0611111121")
                .email("dawood@gmail.com")
                .password("password")
                .isActive(true)
                .roles(List.of(Role.ROLE_CUSTOMER))
                .dayLimit(300)
                .transactionLimit(300)
                .build();
        userService.SaveUser(seedCustomer);
        return seedCustomer;
    }
    private User seedEmployeeCustomer() {
        User seedEmployeeCustomer = User.builder()
                .bsn("277545146")
                .firstName("Ugur")
                .lastName("Say")
                .dateOfBirth(LocalDate.of(2005, 1, 1))
                .phoneNumber("0611111111")
                .email("ugur@gmail.com")
                .password("password")
                .isActive(true)
                .roles(List.of(Role.ROLE_EMPLOYEE, Role.ROLE_CUSTOMER))
                .dayLimit(1000)
                .transactionLimit(300)
                .build();
        userService.SaveUser(seedEmployeeCustomer);
        return seedEmployeeCustomer;
    }
}
