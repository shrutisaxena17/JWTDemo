package com.example.JWTDemo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Roles {
    @Id
    @Column(name = "role", nullable = false, length = 50)
    private String role;

    // Constructors
    public Roles() {}

    public Roles(String role) {
        this.role = role;
    }

    // Getters and setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Roles roles = (Roles) o;
        return role.equals(roles.role);
    }

    @Override
    public int hashCode() {
        return role.hashCode();
    }
}
