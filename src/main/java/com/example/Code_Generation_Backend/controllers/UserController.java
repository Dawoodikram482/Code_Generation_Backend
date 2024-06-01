package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.AccountCreatingDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDetailsDTO;
import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.services.UserService;
import com.example.Code_Generation_Backend.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
@RequestMapping("/users")
@ControllerAdvice
public class UserController {
    private final String DEFAULT_OFFSET_STRING = "0";
    private final String DEFAULT_LIMIT_STRING = "50";
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers(
            @RequestParam(defaultValue = DEFAULT_LIMIT_STRING, required = false) int limit,
            @RequestParam(defaultValue = DEFAULT_OFFSET_STRING, required = false) int offset,
            @RequestParam(required = false) String roles) {
        Role passingRole = null;
        try {
            if (roles != null) {
                passingRole = Role.valueOf(roles.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "The role is not valid"); // this will be caught by RestControllerExceptionHandler
        }
        List<User> users = userService.getAllUsers(limit, offset, passingRole);
        return ResponseEntity.ok(
                users.parallelStream().map(mapUserObjectToDTO).toList()
                // using Parallel Stream to improve performance
        );
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<Object> getPendingApprovals(
            @RequestParam(defaultValue = DEFAULT_LIMIT_STRING, required = false) int limit,
            @RequestParam(defaultValue = DEFAULT_OFFSET_STRING, required = false) int offset) {

        // Get users with isApproved set to false
        List<User> pendingApprovals = userService.getUsersByApprovalStatus(limit, offset, false);
        // Map users to DTOs
        return ResponseEntity.ok(
                pendingApprovals.parallelStream().map(mapUserObjectToDTO).toList()
                // using Parallel Stream to improve performance
        );
    }

    @PostMapping("/{userId}/approve")
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE')")
    public ResponseEntity<Object> approveUser(@PathVariable Long userId, @RequestBody AccountCreatingDTO creatingDTO) {
        try {
            userService.approveUser(userId, creatingDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new Object[0]);
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            if (e instanceof AuthenticationException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/test-employee-role")
    @PreAuthorize(value = "hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity<String> testEmployeeRole() {
        return ResponseEntity.ok("Role EMPLOYEE is recognized");
    }

    // Endpoint to get the authenticated user's details to display in the account overview
    @GetMapping("/myAccountOverview")
    @PreAuthorize(value = "hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<UserDetailsDTO> getMyDetails(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        UserDetailsDTO userDetailsDTO = userService.getUserDetails(user);
        return ResponseEntity.ok(userDetailsDTO);
    }

    private final Function<User, UserDTO> mapUserObjectToDTO = user ->
            new UserDTO(user.getId(), user.getBsn(), user.getFirstName(), user.getLastName(),
                    user.getDateOfBirth(), user.getPhoneNumber(), user.getEmail(), user.isActive(),
                    user.getDayLimit(), user.isApproved(), user.getTransactionLimit()
            );
}
