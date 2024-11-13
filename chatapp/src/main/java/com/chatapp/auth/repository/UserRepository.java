package com.chatapp.auth.repository;

import com.chatapp.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationCode(String verificationCode);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
}
