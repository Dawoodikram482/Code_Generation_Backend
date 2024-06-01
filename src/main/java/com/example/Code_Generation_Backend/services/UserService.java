package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.AccountCreatingDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.RegisterDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDetailsDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDetailsDTO.AccountDTO;
import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

  public User getUserById(Long Id) {
    return userRepository.findById(Id).orElseThrow(() -> new EntityNotFoundException("User with id: " + Id + " not found"));
  }

  public boolean approveUser(Long userId, AccountCreatingDTO accountCreatingDTO) {
    try {
      User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " not found"));
      user.setIsApproved(true);
      user.setDayLimit(accountCreatingDTO.dayLimit());
      user.setTransactionLimit(accountCreatingDTO.transactionLimit());
      user.setRole(Role.ROLE_CUSTOMER);
      accountService.createAccount(accountCreatingDTO);
      userRepository.save(user);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public UserDetailsDTO getUserDetails(User user) {
    List<AccountDTO> accountDTOs = user.getAccounts().stream().map(account ->
            AccountDTO.builder()
                    .iban(account.getIban())
                    .accountBalance(account.getAccountBalance())
                    .accountType(account.getAccountType())
                    .build()
    ).collect(Collectors.toList());

    return UserDetailsDTO.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .accounts(accountDTOs)
            .build();
  }

  public List<User> getUsersByApprovalStatus(int limit, int offset, boolean isApproved) {
    PageRequest pageRequest = PageRequest.of(offset / limit, limit);
    Page<User> users;
    // Fetch users by approval status from the database
    users = userRepository.findByIsApproved(isApproved, pageRequest);
    return users.getContent();
  }
}
