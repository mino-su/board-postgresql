package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.reply.Reply;
import com.example.board.model.user.*;
import com.example.board.service.PostService;
import com.example.board.service.ReplyService;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final ReplyService replyService;

    @PostMapping
    public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequestBody userSignUpRequestBody) {
        var user = userService.signUp(userSignUpRequestBody.username(), userSignUpRequestBody.password());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserAuthenticationResponse> authenticate(@Valid @RequestBody UserLoginRequestBody userLoginRequestBody) {
        var response = userService.authenticate(userLoginRequestBody.username(), userLoginRequestBody.password());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) String query, Authentication authentication) {
        var user = userService.getUsers(query, (UserEntity)authentication.getPrincipal());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username,Authentication authentication) {
        var user = userService.getUser(username, (UserEntity)authentication.getPrincipal());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<List<Post>> getPostsByUsername(@PathVariable String username,Authentication authentication) {
        var posts = postService.getPostByUsername(username, (UserEntity) authentication.getPrincipal());
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PatchMapping("/{username}")
    public ResponseEntity<User> updateUser(
            @PathVariable String username,
            @RequestBody UserPatchRequestBody requestBody,
            Authentication authentication) {
        var user =
                userService.updateUser(username, requestBody, (UserEntity) authentication.getPrincipal());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{username}/replies")
    public ResponseEntity<List<Reply>> getRepliesByUser(@PathVariable String username) {
        var replies = replyService.getRepliesByUser(username);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/{username}/liked-users")
    public ResponseEntity<List<LikedUser>> getLikedUsersByUser(@PathVariable String username, Authentication authentication) {
        var likedUsers = userService.getLikedUsersByUser(username, (UserEntity) authentication.getPrincipal());
        return new ResponseEntity<>(likedUsers, HttpStatus.OK);
    }


    @GetMapping("/{username}/followers")
    public ResponseEntity<List<Follower>> getFollowersByUser(@PathVariable String username, Authentication authentication) {
        var followers = userService.getFollowersByUser(username, (UserEntity) authentication.getPrincipal());
        return new ResponseEntity<>(followers, HttpStatus.OK);
    }

    @GetMapping("/{username}/followings")
    public ResponseEntity<List<Follower>> getFollowingsByUser(@PathVariable String username, Authentication authentication) {
        var followings = userService.getFollowingsByUser(username, (UserEntity)authentication.getPrincipal() );
        return new ResponseEntity<>(followings, HttpStatus.OK);
    }

    @PostMapping("/{username}/follows")
    public ResponseEntity<User> follow(@PathVariable String username, Authentication authentication) {
        var user = userService.follow(username, (UserEntity) authentication.getPrincipal());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{username}/follows")
    public ResponseEntity<User> unfollow(
            @PathVariable String username, Authentication authentication) {
        var user = userService.unfollow(username, (UserEntity) authentication.getPrincipal());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
