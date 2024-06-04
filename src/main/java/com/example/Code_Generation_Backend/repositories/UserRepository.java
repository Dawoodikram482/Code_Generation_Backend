package com.example.Code_Generation_Backend.repositories;

import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.models.User;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {
Optional<User> findByEmail(String email);
//    Optional<User> findByBsn(String bsn);
//    boolean existsByEmailEqualsIgnoreCase(String email);
//    boolean existsByBsn(String bsn);
    Page<User> findByRole(@NonNull Role role, @NonNull Pageable pageable);

    //to check during registration if user already exist based on their bsn and email
    boolean existsByBsn(String bsn);
    boolean existsByEmail(String email);


  Page<User> findByRoles(Role passingRole, Pageable pageRequest);

  Page<User> findByIsApproved(boolean isApproved, Pageable pageRequest);

}

