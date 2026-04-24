package com.grid07.guardrail.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grid07.guardrail.DTO.AddCommentRequest;
import com.grid07.guardrail.DTO.CreatePostRequest;
import com.grid07.guardrail.DTO.LikeRequest;
import com.grid07.guardrail.Service.PostService;


@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService = new PostService();

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId,@RequestBody AddCommentRequest request) {
        return postService.addComment(postId, request);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId,@RequestBody LikeRequest request) {
        return postService.likePost(postId, request);
    }
}
