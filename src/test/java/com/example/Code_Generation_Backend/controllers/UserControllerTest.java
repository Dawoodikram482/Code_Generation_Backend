package com.example.Code_Generation_Backend.controllers;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.CustomerRegistrationDTO;
import com.example.Code_Generation_Backend.config.SecurityConfig;
import com.example.Code_Generation_Backend.jwtFilter.JwtTokenFilter;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.repositories.UserRepository;
import com.example.Code_Generation_Backend.security.JwtProvider;
import com.example.Code_Generation_Backend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    @MockBean
    private UserService userService;

/*    @MockBean
    private UserRepository userRepository;*/
    private CustomerRegistrationDTO registrationDTO;
    private User registeredUser;

    @BeforeEach
    void setUp() {
        registrationDTO = new CustomerRegistrationDTO();
        registrationDTO.setFirstName("Aura");
        registrationDTO.setLastName("Example");
        registrationDTO.setEmail("jancokjancok@example.com");
        registrationDTO.setPhoneNumber("5435332");
        registrationDTO.setBsn("5895498");
        registrationDTO.setBirthDate(LocalDate.of(2000, 1, 1));
        registrationDTO.setPassword("password");

        registeredUser = new User();
        registeredUser.setFirstName("Aura");
        registeredUser.setLastName("Example");
        registeredUser.setEmail("aura@example.com");
        registeredUser.setPhoneNumber("5435332");
        registeredUser.setBsn("221001");
        registeredUser.setDateOfBirth(LocalDate.of(2000, 1, 1));
        registeredUser.setPassword("password");
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
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

  /*  @Test
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
}
