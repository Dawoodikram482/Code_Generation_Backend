package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.config.SecurityConfig;
import com.example.Code_Generation_Backend.jwtFilter.JwtTokenFilter;
import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.AccountType;
import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.security.JwtProvider;
import com.example.Code_Generation_Backend.services.AccountService;
import com.example.Code_Generation_Backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = AccountController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {JwtProvider.class, JwtTokenFilter.class})
)
@Import(SecurityConfig.class)
@EnableMethodSecurity
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    private Account testAccount;

    private User testUser;

    @BeforeEach
    void setUp() {

        /*testUser = User.builder()
                .bsn("509547989")
                .firstName("Dawood")
                .lastName("Ikram")
                .dateOfBirth(LocalDate.of(2003, 7, 16))
                .phoneNumber("0611111121")
                .email("dawood@gmail.com")
                .password("password")
                .isActive(true)
                .isApproved(true)
                .roles(List.of(Role.ROLE_CUSTOMER))
                .dayLimit(300)
                .transactionLimit(300)
                .build();

        testAccount = new Account();
        testAccount.setIban("NL91INH0417164300");
        testAccount.setApproved("NL91INH0417164300");*/
    }

    @Test
    @WithMockUser(username = "alfinaaura@gmail.com", roles = {"CUSTOMER"})
    void testSearchIban_Success() throws Exception {
        // Mock service call
        when(accountService.getIbanByName("alfina", "aura")).thenReturn(testAccount.getIban());

        // Perform the request and verify the response
        mockMvc.perform(get("/accounts/search-iban")
                        .param("firstName", "alfina")
                        .param("lastName", "aura"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("NL91INH0417164300"));
    }

    /*@Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testSearchIbanNotFound() throws Exception {
        when(accountService.getIbanByName("Jane", "Smith"))
                .thenThrow(new AccountNotFoundException("User does not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/search-iban")
                        .param("firstName", "Jane")
                        .param("lastName", "Smith")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("User does not exist"));
    }*/
}
