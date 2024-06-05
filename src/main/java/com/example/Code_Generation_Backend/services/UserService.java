package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.AccountCreatingDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.RegisterDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDetailsDTO;
import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final AccountService accountService;

  public UserService(UserRepository userRepository, AccountService accountService) {
    this.userRepository = userRepository;
    this.accountService = accountService;
  }

  private final Function<RegisterDTO, User> registerDTOUserFunction = registerationDTO -> User.builder()
          .bsn(registerationDTO.bsn())
          .email(registerationDTO.email())
          .password(registerationDTO.password())
          .firstName(registerationDTO.firstName())
          .lastName(registerationDTO.lastName())
          .phoneNumber(registerationDTO.phoneNumber())
          .dateOfBirth(LocalDate.parse(registerationDTO.dateOfBirth()))
          .build();

  public User SaveUser(User user) {
    return userRepository.save(user);
  }

  public List<User> getAllUsers(int limit, int offset, Role passingRole) {
    PageRequest pageRequest = PageRequest.of(offset / limit, limit);
    Page<User> users;
    if (passingRole != null) {
      users = userRepository.findByRoles(passingRole, pageRequest);
    } else {
      users = userRepository.findAll(pageRequest);
    }
    return users.getContent();
  }

  public User getUserById(Long Id){
    return userRepository.findById(Id).orElseThrow(() -> new EntityNotFoundException("User with id: " + Id + " not found"));
  }

  public boolean approveUser(Long userId, AccountCreatingDTO accountCreatingDTO) {
    try {
      User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " not found"));
      user.setIsApproved(true);
      user.setDayLimit(accountCreatingDTO.dayLimit());
      user.setTransactionLimit(accountCreatingDTO.transactionLimit());
      user.setRole(Role.ROLE_CUSTOMER);
      // Create checking account
      AccountCreatingDTO checkingAccountDTO = new AccountCreatingDTO(
              accountCreatingDTO.dayLimit(),
              accountCreatingDTO.absoluteLimit(),
              accountCreatingDTO.transactionLimit(),
              "CURRENT",
              accountCreatingDTO.accountHolderId()
      );
      accountService.createAccount(checkingAccountDTO);

      // Create savings account
      AccountCreatingDTO savingsAccountDTO = new AccountCreatingDTO(
              accountCreatingDTO.dayLimit(),
              accountCreatingDTO.absoluteLimit(),
              accountCreatingDTO.transactionLimit(),
              "SAVINGS",
              accountCreatingDTO.accountHolderId()
      );
      accountService.createAccount(savingsAccountDTO);

      return true;
    }catch (Exception e){
      return false;
    }
  }

  // New method to get user details for the authenticated user
  //this will be called as soon as client login and their Account overview displayed
  public UserDetailsDTO getUserDetails(User user) {
    Account account = user.getAccounts().stream().findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Account not found for user: " + user.getEmail()));

    return UserDetailsDTO.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .iban(account.getIban())
            .accountBalance(account.getAccountBalance())
            .accountType(account.getAccountType())
            .build();
  }

  public List<User> getUsersByApprovalStatus(int limit, int offset, boolean isApproved) {
    PageRequest pageRequest = PageRequest.of(offset / limit, limit);
    Page<User> users;
    // Fetch users by approval status from the database
    users= userRepository.findByIsApproved(isApproved, pageRequest);
    return users.getContent();
  }
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User with email: " + email + " not found"));
  }

  public List<User> getAllUsers(Pageable pageable, Role passingRole) {
    Page<User> users;
    if (passingRole != null) {
      users = userRepository.findByRoles(passingRole, pageable);
    } else {
      users = userRepository.findAll(pageable);
    }
    return users.getContent();
  }
}
