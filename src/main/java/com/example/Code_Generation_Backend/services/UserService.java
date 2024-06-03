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

  /*private final Function<RegisterDTO, User> registerDTOUserFunction = registerationDTO -> User.builder()
          .bsn(registerationDTO.bsn())
          .email(registerationDTO.email())
          .password(registerationDTO.password())
          .firstName(registerationDTO.firstName())
          .lastName(registerationDTO.lastName())
          .phoneNumber(registerationDTO.phoneNumber())
          .dateOfBirth(LocalDate.parse(registerationDTO.dateOfBirth()))
          .build();*/

  public User SaveUser(User user)
  {
    return userRepository.save(user);
  }

  /*public List<User> getAllUsers(int limit, int offset, Role passingRole) {
    PageRequest pageRequest = PageRequest.of(offset / limit, limit);
    Page<User> users;
    if (passingRole != null) {
      users = userRepository.findByRoles(passingRole, pageRequest);
    } else {
      users = userRepository.findAll(pageRequest);
    }
    return users.getContent();
  }*/

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
}

  /*public List<User> getAllUsers(Pageable pageable, Role passingRole) {
    Page<User> users;
    if (passingRole != null) {
      users = userRepository.findByRoles(passingRole, pageable);
    } else {
      users = userRepository.findAll(pageable);
    }
    return users.getContent();
  }

  /*public User registerNewCustomer(CustomerRegistrationDTO dto) {
    User user = new User();
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setEmail(dto.getEmail());
    user.setPhoneNumber(dto.getPhoneNumber());
    user.setBsn(dto.getBsn());
    user.setDateOfBirth(dto.getBirthDate());
    user.setRoles(List.of(Role.ROLE_CUSTOMER));
    user.setApproved(false);
    user.setActive(true); // Assuming default active status is true

    user = userRepository.save(user);

    if (dto.getAccountType().equalsIgnoreCase("Savings") || dto.getAccountType().equalsIgnoreCase("Both")) {
      Account savingsAccount = new Account();
      savingsAccount.setUser(user);
      savingsAccount.setAccountType(AccountType.SAVINGS);
      accountRepository.save(savingsAccount);
    }

    if (dto.getAccountType().equalsIgnoreCase("Current") || dto.getAccountType().equalsIgnoreCase("Both")) {
      Account currentAccount = new Account();
      currentAccount.setUser(user);
      currentAccount.setAccountType(AccountType.CURRENT);
      accountRepository.save(currentAccount);
    }

    return user;
  }

  public void approveCustomer(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with ID " + userId));
    user.setApproved(true);
    userRepository.save(user);
  }
}*/



