package com.grid07.guardrail.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id",nullable = false)
    private Long postId;

    @Column(name = "author_id",nullable = false)
    private Long authorId;

    @Column(name = "author_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorType authorType;
    
    private String content;

    @Column(name = "depth_level")
    private int depthLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
