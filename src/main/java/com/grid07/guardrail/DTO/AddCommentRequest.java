package com.grid07.guardrail.DTO;

import com.grid07.guardrail.Entity.AuthorType;

import lombok.Data;

@Data
public class AddCommentRequest {
    private Long authorId;
    private AuthorType authorType;
    private Long targetHumanId;   // which human user | bot is replying to
    private String content;
    private int depthLevel;
}