package com.grid07.guardrail.DTO;

import com.grid07.guardrail.Entity.AuthorType;

import lombok.Data;

@Data
public class LikeRequest {
    private Long userId;
    private AuthorType authorType;
}
