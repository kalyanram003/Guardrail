package com.grid07.guardrail.Controller;

import com.grid07.guardrail.DTO.AddCommentRequest;
import com.grid07.guardrail.DTO.CreatePostRequest;
import com.grid07.guardrail.DTO.LikeRequest;
import com.grid07.guardrail.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long postId,
            @RequestBody AddCommentRequest request) {
        return postService.addComment(postId, request);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(
            @PathVariable Long postId,
            @RequestBody LikeRequest request) {
        return postService.likePost(postId, request);
    }
}