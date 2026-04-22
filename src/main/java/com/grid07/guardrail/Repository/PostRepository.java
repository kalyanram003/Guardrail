package com.grid07.guardrail.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grid07.guardrail.Entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    
}

