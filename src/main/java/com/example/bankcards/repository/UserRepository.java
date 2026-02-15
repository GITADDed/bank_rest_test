package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByDeletedFalse(Pageable pageable);
    Optional<User> findByIdAndDeletedFalse(Long id);
    Optional<User> findByUsernameAndDeletedFalse(String username);
}
