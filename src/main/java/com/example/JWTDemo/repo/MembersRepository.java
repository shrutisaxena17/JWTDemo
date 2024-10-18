package com.example.JWTDemo.repo;

import com.example.JWTDemo.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members,String> {
    // Custom query to find a member by userId
    Optional<Members> findByUserId(String userId);
}

