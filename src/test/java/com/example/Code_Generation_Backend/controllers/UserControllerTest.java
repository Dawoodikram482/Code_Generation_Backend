package com.example.Code_Generation_Backend.controllers;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.AccountCreatingDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.CustomerRegistrationDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.UserLimitsDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDetailsDTO;
import com.example.Code_Generation_Backend.config.SecurityConfig;
import com.example.Code_Generation_Backend.jwtFilter.JwtTokenFilter;
import com.example.Code_Generation_Backend.models.AccountType;
import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.repositories.UserRepository;
import com.example.Code_Generation_Backend.security.JwtProvider;
import com.example.Code_Generation_Backend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Limit;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {AccountController.class, JwtProvider.class, JwtTokenFilter.class})
)
@Import(SecurityConfig.class)
@EnableMethodSecurity
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    private CustomerRegistrationDTO registrationDTO;
    private User registeredUser;

    @MockBean
    private UserDetailsService userDetailsService;

    private User testUser;
    private UserDetailsDTO userDetailsDTO;
    private User mockUser;
    private UserLimitsDTO userLimitsDTO;

    @BeforeEach
    void setUp() {
        registrationDTO = new CustomerRegistrationDTO();
        registrationDTO.setFirstName("Aura");
        registrationDTO.setLastName("Example");
        registrationDTO.setEmail("jancokjancok@gmail.com");
        registrationDTO.setPhoneNumber("5435332");
        registrationDTO.setBsn("5895498");
        registrationDTO.setBirthDate(LocalDate.of(2000, 1, 1));
        registrationDTO.setPassword("password");

        registeredUser = new User();
        registeredUser.setFirstName("Aura");
        registeredUser.setLastName("Example");
        registeredUser.setEmail("aura@gmail.com");
        registeredUser.setPhoneNumber("5435332");
        registeredUser.setBsn("221001");
        registeredUser.setDateOfBirth(LocalDate.of(2000, 1, 1));
        registeredUser.setPassword("password");

        // New setup for getMyDetails test
        testUser = new User();
        testUser.setFirstName("Aura");
        testUser.setLastName("Alfina");
        testUser.setEmail("alfinaauraoverview@gmail.com");
        testUser.setPhoneNumber("5435332");
        testUser.setBsn("221001");
        testUser.setDateOfBirth(LocalDate.of(2000, 1, 1));
        testUser.setPassword("password");

        userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setFirstName("Aura");
        userDetailsDTO.setLastName("Alfina");
        userDetailsDTO.setEmail("alfinaauraoverview@gmail.com");
        userDetailsDTO.setPhoneNumber("5435332");
        userDetailsDTO.setDateOfBirth(LocalDate.of(2000, 1, 1));
        userDetailsDTO.setBsn("221001");
        userDetailsDTO.setAccounts(Collections.singletonList(
                UserDetailsDTO.AccountDTO.builder()
                        .iban("NL01BANK0123456789")
                        .accountBalance(1000.0)
                        .accountType(AccountType.CURRENT)
                        .build()
        ));
        mockUser = new User();
        mockUser.setFirstName("Dipika");
        mockUser.setLastName("Bhandari");
        mockUser.setEmail("db@gmail.com");
        mockUser.setPhoneNumber("123456789");
        mockUser.setBsn("9987654123");
        mockUser.setDateOfBirth(LocalDate.of(2003, 12, 12));
        mockUser.setPassword("password");
        mockUser.setRoles(List.of(Role.ROLE_EMPLOYEE));
        mockUser.setApproved(true);
        mockUser.isActive();
        mockUser.setTransactionLimit(200);
        mockUser.setDayLimit(100);

        userLimitsDTO = new UserLimitsDTO(100.0);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    void testRegisterUser() throws Exception {
        when(userService.registerNewCustomer(registrationDTO)).thenReturn(registeredUser);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(registrationDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType("application/json")
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Your registration is successful and being processed"));
    }

   /*@Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testRegisterUserWithExistingEmailOrBsn() throws Exception {

        when(userService.registerNewCustomer(registrationDTO)).thenThrow(new DataIntegrityViolationException("User already exists."));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(registrationDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType("application/json")
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("User with BSN " + registrationDTO.getBsn() + " or email " + registrationDTO.getEmail() + " already exists."));
    }*/

    @Test
    @WithMockUser(username = "alfinaauraoverview@gmail.com", roles = {"CUSTOMER"})
    void testGetMyDetails() throws Exception {
        // Mocking the UserService to return the testUser and UserDetailsDTO when appropriate methods are called
        when(userService.getUserByEmail(testUser.getEmail())).thenReturn(testUser);
        when(userService.getUserDetails(testUser)).thenReturn(userDetailsDTO);

        // Mocking the UserDetailsService to return a UserDetails object
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(testUser.getEmail())
                .password(testUser.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                .build();

        when(userDetailsService.loadUserByUsername(testUser.getEmail())).thenReturn(userDetails);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/myAccountOverview")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Aura"))
                .andExpect(jsonPath("$.lastName").value("Alfina"))
                .andExpect(jsonPath("$.email").value("alfinaauraoverview@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("5435332"))
                .andExpect(jsonPath("$.dateOfBirth").value("2000-01-01"))
                .andExpect(jsonPath("$.bsn").value("221001"))
                .andExpect(jsonPath("$.accounts[0].iban").value("NL01BANK0123456789"))
                .andExpect(jsonPath("$.accounts[0].accountBalance").value(1000.0))
                .andExpect(jsonPath("$.accounts[0].accountType").value("CURRENT"));
    }
    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void approveUserReturns_Ok() throws Exception{
        when(userService.approveUser(any(Long.class),any(AccountCreatingDTO.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/users/approve/1")
                        .contentType("application/json")
                        .content("{ \"dayLimit\": 100, \"absoluteLimit\": 200, \"transactionLimit\": 300, \"accountHolderId\": 1 }")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void approveUser_ReturnsForbidden_When_UserNotFound() throws Exception {
        // Mock userService.approveUser() to throw EntityNotFoundException
        when(userService.approveUser(any(Long.class), any(AccountCreatingDTO.class))).thenThrow(EntityNotFoundException.class);

        // Perform POST request to /approve/{id} endpoint with mock JSON data
        mockMvc.perform(MockMvcRequestBuilders.post("/approve/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"dayLimit\": 100, \"absoluteLimit\": 200, \"transactionLimit\": 300, \"accountHolderId\": 1 }")
                .accept(MediaType.APPLICATION_JSON))
            // Verify response status is UNAUTHORIZED (401)
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "db@gmail.com",password = "password", roles = "EMPLOYEE")
    public void updateDailyLimits_ReturnsOk_When_ValidInput() throws Exception {
        when(userService.updateDailyLimit(1L, userLimitsDTO)).thenReturn(mockUser);

        mockMvc.perform(put("/users/1/limits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLimitsDTO))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

}
