package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.service.JwtService;
import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;

    private final JwtService jwtService;


    @GetMapping("")
    public ResponseEntity<List<Post>> getPost() {
        log.info("GET /api/v1/posts");
        var posts = postService.getPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId) {
//        log.info("GET /api/v1/posts/{}",postId);
        var post = postService.getPostByPostId(postId);
        return ResponseEntity.ok(post);

    }

    // POST /posts
    @PostMapping("")
    public ResponseEntity<Post> createPost(
            @RequestBody PostPostRequestBody postPostRequestBody,
            Authentication authentication) {
//        log.info("POST /api/v1/posts");
        var post = postService.createPost(postPostRequestBody, (UserEntity)authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId,
                                           @RequestBody PostPostRequestBody postPostRequestBody,
                                           Authentication authentication) {
//        log.info("PATCH /api/v1/posts/{}",postId);
        var post = postService.updatePost(postId, postPostRequestBody, (UserEntity)authentication.getPrincipal());

        return ResponseEntity.ok(post);

    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, Authentication authentication) {
//        log.info("DELETE /api/v1/posts/{}",postId);
        postService.deletePost(postId, (UserEntity)authentication.getPrincipal());
        return ResponseEntity.noContent().build();
    }
}
