package com.grid07.guardrail.Service;

import org.springframework.http.ResponseEntity;

import com.grid07.guardrail.DTO.AddCommentRequest;
import com.grid07.guardrail.DTO.CreatePostRequest;
import com.grid07.guardrail.DTO.LikeRequest;
import com.grid07.guardrail.Entity.Comment;
import com.grid07.guardrail.Entity.Post;
import com.grid07.guardrail.Repository.CommentRepository;
import com.grid07.guardrail.Repository.PostRepository;

public class PostService {
    private final PostRepository postRepository = null;
    private final CommentRepository commentRepository = null;

    public Post createPost(CreatePostRequest request) {
        Post post = new Post();
        post.setAuthorId(request.getAuthorId());
        post.setAuthorType(request.getAuthorType());
        post.setContent(request.getContent());
        return postRepository.save(post);
    }

    public ResponseEntity<?> addComment(Long postId, AddCommentRequest request) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(request.getAuthorId());
        comment.setAuthorType(request.getAuthorType());
        comment.setContent(request.getContent());
        comment.setDepthLevel(request.getDepthLevel());
        commentRepository.save(comment);
        return ResponseEntity.ok(comment);
    }

    public ResponseEntity<?> likePost(Long postId, LikeRequest request) {
        return ResponseEntity.ok("Post " + postId + " liked by user " + request.getUserId());
    }
}
