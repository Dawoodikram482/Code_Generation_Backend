package com.example.Code_Generation_Backend.controllers;

import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDTO;
import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.services.UserService;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/updateRole")
    public ResponseEntity<Object> updateUserRole(@RequestBody @Validated UserDTO updateUserRoleDTO) {
        try {
            Role newRole = Role.valueOf(updateUserRoleDTO.getNewRole().toUpperCase());
            User updatedUser = userService.updateUserRole(updateUserRoleDTO.getUserId(), newRole);
            return ResponseEntity.ok(mapUserObjectToDTO.apply(updatedUser));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The role is not valid");
        }
    }

    private final Function<User, UserDTO> mapUserObjectToDTO = user -> new UserDTO(user.getBsn(), user.getFirstName(), user.getLastName(), user.getDateOfBirth(),user.getPhoneNumber(), user.getEmail(), user.getRole());
}
