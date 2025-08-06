package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.user.*;
import com.example.board.service.PostService;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping()
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false,name = "query") String query) {
        var users = userService.getUsers(query);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        var users = userService.getUser(username);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username,
                                           @RequestBody UserPatchRequestBody userPatchRequestBody,
                                           Authentication authentication) {
        var users = userService.updateUser(username, userPatchRequestBody, (UserEntity)authentication.getPrincipal());
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{username}/posts")
    public ResponseEntity<List<Post>> getPostByUsername(@PathVariable String username) {
       var posts =  postService.getPostByUsername(username);
        return ResponseEntity.ok(posts);
    }

}
