package com.grid07.guardrail.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grid07.guardrail.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}

