package com.example.Code_Generation_Backend.repositories;

import com.example.Code_Generation_Backend.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByBsn(String bsn);
    boolean existsByEmailEqualsIgnoreCase(String email);
    boolean existsByBsn(String bsn);
}
