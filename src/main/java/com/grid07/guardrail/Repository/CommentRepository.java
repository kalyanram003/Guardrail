package com.grid07.guardrail.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grid07.guardrail.Entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
}