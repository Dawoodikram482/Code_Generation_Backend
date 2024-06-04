package com.example.Code_Generation_Backend.services;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.AccountCreatingDTO;
import com.example.Code_Generation_Backend.DTOs.requestDTOs.CustomerRegistrationDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDetailsDTO;
import com.example.Code_Generation_Backend.DTOs.responseDTOs.UserDetailsDTO.AccountDTO;
import com.example.Code_Generation_Backend.models.Account;
import com.example.Code_Generation_Backend.models.AccountType;
import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import com.example.Code_Generation_Backend.repositories.UserRepository;
import com.example.Code_Generation_Backend.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;


@Service
public class UserService
{

  private final UserRepository userRepository;
  private final AccountService accountService;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, AccountService accountService)
  {
    this.userRepository = userRepository;
    this.accountService = accountService;
  }


  public User SaveUser(User user)
  {
    return userRepository.save(user);
  }


  public User getUserById(Long Id)
  {
    return userRepository.findById(Id).orElseThrow(() -> new EntityNotFoundException("User with id: " + Id + " not found"));
  }

  public boolean approveUser(Long userId, AccountCreatingDTO accountCreatingDTO)
  {
    try
    {
      User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " not found"));
      user.setIsApproved(true);
      user.setDayLimit(accountCreatingDTO.dayLimit());
      user.setTransactionLimit(accountCreatingDTO.transactionLimit());
      user.setRole(Role.ROLE_CUSTOMER);
      accountService.createAccount(accountCreatingDTO);
      userRepository.save(user);
      return true;
    } catch (Exception e)
    {
      return false;
    }
  }

  //this will be called as soon as client login and their Account overview displayed
  @Transactional
  public UserDetailsDTO getUserDetails(User user)
  {
    // Explicitly initialize accounts collection
    List<Account> accounts = user.getAccounts();
    accounts.size();

    List<AccountDTO> accountDTOs = user.getAccounts().stream().map(account ->
            new AccountDTO(
                    account.getIban(),
                    account.getAccountBalance(),
                    account.getAccountType()
            )
    ).collect(Collectors.toList());

    return UserDetailsDTO.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .dateOfBirth(user.getDateOfBirth())
            .bsn(user.getBsn())
            .accounts(accountDTOs)
            .build();
  }


  public List<User> getUsersByApprovalStatus(int limit, int offset, boolean isApproved)
  {
    PageRequest pageRequest = PageRequest.of(offset / limit, limit);
    Page<User> users;
    // Fetch users by approval status from the database
    users = userRepository.findByIsApproved(isApproved, pageRequest);
    return users.getContent();
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User with email: " + email + " not found"));
  }


 /* public List<User> getAllUsers(Pageable pageable, Role passingRole) {
    Page<User> users;
    if (passingRole != null) {
      users = userRepository.findByRoles(passingRole, pageable);
    } else {
      users = userRepository.findAll(pageable);
    }
    return users.getContent();
  }*/

  public User registerNewCustomer(CustomerRegistrationDTO dto) {
    // Check if a user with the same BSN or email already exists
    if (userRepository.existsByBsn(dto.getBsn())) {
      throw new DataIntegrityViolationException("User with BSN " + dto.getBsn() + " already exists.");
    }
    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new DataIntegrityViolationException("User with email " + dto.getEmail() + " already exists.");
    }
    User user = new User();
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setEmail(dto.getEmail());
    user.setPhoneNumber(dto.getPhoneNumber());
    user.setBsn(dto.getBsn());
    user.setDateOfBirth(dto.getBirthDate());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setRoles(List.of(Role.ROLE_CUSTOMER));
    user.setApproved(false);
    user.setActive(true); // Assuming default active status is true
    // Save the user first to get an ID for the relationships
    user = userRepository.save(user);
    return user;
  }


}



