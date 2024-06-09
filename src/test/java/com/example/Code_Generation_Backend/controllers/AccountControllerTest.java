package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.AbsoluteLimitRequestDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionAccountDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.TransactionResponseDTO;
import com.example.Code_Generation_Backend.config.SecurityConfig;
import com.example.Code_Generation_Backend.jwtFilter.JwtTokenFilter;
import com.example.Code_Generation_Backend.models.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @MockBean
    private UserService userService;

    private Account testAccount;
    private Account testAccount2;
    private Account testAccount3;

    private User user;
    private User user2;
    private User user3;
    private Account testAccount4;

    @BeforeEach
    void init() {
        user = User.builder()
                .bsn("123456789")
                .firstName("Dipika")
                .lastName("Bhandari")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .phoneNumber("9987654123")
                .email("db@gmail.com")
                .password("password")
                .isActive(true)
                .roles(List.of(Role.ROLE_EMPLOYEE))
                .transactionLimit(99999999)
                .dayLimit(99999999)
                .isApproved(true)
                .build();

        testAccount = new Account("NL01UNIB123456789", 1000.0, LocalDate.now(), 500.0, true, AccountType.CURRENT, user);

        user2 = User.builder()
                .bsn("582022290")
                .firstName("Solaiman")
                .lastName("Hossain")
                .dateOfBirth(LocalDate.of(2003, 10, 1))
                .phoneNumber("0611111121")
                .email("Solaiman@hossain.com")
                .password("secretword")
                .isActive(true)
                .isApproved(true)
                .dayLimit(300)
                .transactionLimit(300)
                .roles(List.of(Role.ROLE_EMPLOYEE))
                .build();

        testAccount2 = new Account("NL01UNIB0000000003", 500.0, LocalDate.now(), 250, true, AccountType.SAVINGS, user2);
        testAccount3 = new Account("NL01UNIB0000000002", 1000.0, LocalDate.now(), 500, true, AccountType.CURRENT, user2);

        user3 = User.builder()
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

        testAccount4 = new Account("NL01DAWO0000000001", 777.0, LocalDate.now(), 100.0, true, AccountType.SAVINGS, user3);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testGetAllAccounts() throws Exception {
        List<Account> accounts = List.of(testAccount, testAccount2);

        when(accountService.getAllAccounts(50, 0, null)).thenReturn(accounts);

        mockMvc.perform(get("/accounts")
                        .param("limit", "50")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(accounts.size()))
                .andExpect(jsonPath("$[0].iban").value(testAccount.getIban()))
                .andExpect(jsonPath("$[1].iban").value(testAccount2.getIban()));
    }

    @Test
    @WithMockUser(username = "db@gmail.com", password = "password", roles = "CUSTOMER")
    void testSearchIban() throws Exception {
        Page<TransactionAccountDTO> accountPage = new PageImpl<>(List.of(new TransactionAccountDTO("NL01UNIB123456789", AccountType.CURRENT, "Dipika Bhandari")));

        when(accountService.getIbansByName(any(Pageable.class), eq("Dipika"), eq("Bhandari"))).thenReturn(accountPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/search-iban")
                        .param("limit", "50")
                        .param("offset", "0")
                        .param("firstName", "Dipika")
                        .param("lastName", "Bhandari"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].iban").value("NL01UNIB123456789"))
                .andExpect(jsonPath("$.content[0].user").value("Dipika Bhandari"));
    }

    @Test
    @WithMockUser(username = "db@gmail.com", password = "password", roles = "EMPLOYEE")
    void testCloseAccount() throws Exception {
        when(accountService.closeAccount("NL01UNIB123456789")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/closeAccount/NL01UNIB123456789")
                        .with(csrf()))
                .andExpect(status().isOk());

        when(accountService.closeAccount("NL01UNIB123456789")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/closeAccount/NL01UNIB123456789")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to close the account"));
    }

    @Test
    @WithMockUser(username = "db@gmail.com", password = "password", roles = "EMPLOYEE")
    void testUpdateAbsoluteLimit() throws Exception {
        AbsoluteLimitRequestDTO requestDTO = new AbsoluteLimitRequestDTO(200.0);
        Account updatedAccount = testAccount;
        updatedAccount.setAbsoluteLimit(200.0);

        when(accountService.getAccountByIBAN("NL01UNIB123456789")).thenReturn(testAccount);
        when(accountService.updateAbsoluteLimit(testAccount, requestDTO)).thenReturn(updatedAccount);

        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/NL01UNIB123456789/limit")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"absoluteLimit\": 200.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.absoluteLimit").value(200.0));
    }

    @Test
    @WithMockUser(username = "db@gmail.com", password = "password", roles = "EMPLOYEE")
    void testGetAccountByStatus() throws Exception {
        List<Account> accounts = List.of(testAccount, testAccount2, testAccount3);

        when(accountService.getAccountByStatus(any(Pageable.class), eq(true))).thenReturn(accounts);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/status")
                        .param("limit", "50")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(accounts.size())))
                .andExpect(jsonPath("$[0].iban").value(testAccount.getIban()))
                .andExpect(jsonPath("$[1].iban").value(testAccount2.getIban()))
                .andExpect(jsonPath("$[2].iban").value(testAccount3.getIban()));
    }
}