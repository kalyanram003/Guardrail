package com.grid07.guardrail.DTO;

import com.grid07.guardrail.Entity.AuthorType;

import lombok.Data;

@Data
public class CreatePostRequest {
    private Long authorId;
    private AuthorType authorType;
    private String content;
}
