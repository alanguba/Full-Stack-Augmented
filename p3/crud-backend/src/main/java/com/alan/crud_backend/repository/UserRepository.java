package com.alan.crud_backend.repository;


import com.alan.crud_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Long> {
}