package com.example.JWTDemo.repo;

import com.example.JWTDemo.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles,String> {
    // Custom query to find a role by its name
    Optional<Roles> findByRole(String role);
}
