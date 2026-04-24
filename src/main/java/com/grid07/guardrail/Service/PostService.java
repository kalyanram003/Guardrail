package com.grid07.guardrail.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.grid07.guardrail.DTO.AddCommentRequest;
import com.grid07.guardrail.DTO.CreatePostRequest;
import com.grid07.guardrail.DTO.LikeRequest;
import com.grid07.guardrail.Entity.AuthorType;
import com.grid07.guardrail.Entity.Comment;
import com.grid07.guardrail.Entity.Post;
import com.grid07.guardrail.Repository.CommentRepository;
import com.grid07.guardrail.Repository.PostRepository;

@Service
public class PostService {
    private final PostRepository postRepository = null;
    private final CommentRepository commentRepository = null;
    private final GuardrailService guardrailService = new GuardrailService();
    private final ViralityService viralityService = new ViralityService();
    private final NotificationService notificationService = new NotificationService();

    
    public Post createPost(CreatePostRequest request) {
        Post post = new Post();
        post.setAuthorId(request.getAuthorId());
        post.setAuthorType(request.getAuthorType());
        post.setContent(request.getContent());
        return postRepository.save(post);
    }

    public ResponseEntity<?> addComment(Long postId, AddCommentRequest request) {
        if (!guardrailService.isDepthAllowed(request.getDepthLevel())) {
            return ResponseEntity.status(429)
                .body("Rejected: Thread depth exceeds " + 20 + " levels.");
        }

        if (request.getAuthorType() == AuthorType.BOT) {
            if (!guardrailService.tryIncrementBotCount(postId)) {
                return ResponseEntity.status(429)
                    .body("Rejected: Bot comment cap (100) reached for this post.");
            }
            if (!guardrailService.trySetCooldown(request.getAuthorId(), request.getTargetHumanId())) {
                guardrailService.decrementBotCount(postId);
                return ResponseEntity.status(429)
                    .body("Rejected: Bot is on cooldown for this user (10 min).");
            }
            Comment comment = buildComment(postId, request);
            commentRepository.save(comment);
            viralityService.incrementBotReply(postId);

            notificationService.handleBotInteraction(request.getTargetHumanId(), request.getAuthorId());
            return ResponseEntity.ok(comment);
        }

        // Human comment, no guardrails needed
        Comment comment = buildComment(postId, request);
        commentRepository.save(comment);
        viralityService.incrementHumanComment(postId);
        return ResponseEntity.ok(comment);
    }

    
    public ResponseEntity<?> likePost(Long postId, LikeRequest request) {
        if (request.getAuthorType() == AuthorType.USER) {
            viralityService.incrementHumanLike(postId);
        }
        return ResponseEntity.ok("Post " + postId + " liked. Virality: "
            + viralityService.getViralityScore(postId));
    }

    private Comment buildComment(Long postId, AddCommentRequest req) {
        Comment c = new Comment();
        c.setPostId(postId);
        c.setAuthorId(req.getAuthorId());
        c.setAuthorType(req.getAuthorType());
        c.setContent(req.getContent());
        c.setDepthLevel(req.getDepthLevel());
        return c;
    }
}
