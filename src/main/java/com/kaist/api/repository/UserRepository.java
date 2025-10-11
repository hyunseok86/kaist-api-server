package com.kaist.api.repository;

import com.kaist.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.userName LIKE %:userName% ORDER BY u.userName ASC")
    Page<User> findByUserNameContaining(@Param("userName") String userName, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.email LIKE %:email% ORDER BY u.email ASC")
    Page<User> findByEmailContaining(@Param("email") String email, Pageable pageable);
}

