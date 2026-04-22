package com.grid07.guardrail.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grid07.guardrail.Entity.Bot;

public interface BotRepository extends JpaRepository<Bot, Long> {
    
}

