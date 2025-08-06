package com.example.board.controller;

import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.reply.Reply;
import com.example.board.model.reply.ReplyPostRequestBody;
import com.example.board.service.JwtService;
import com.example.board.service.PostService;
import com.example.board.service.ReplyService;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/replies")
@RequiredArgsConstructor
@Slf4j
public class ReplyController {

    private final ReplyService replyService;


//    @GetMapping("")
//    public ResponseEntity<List<Reply>> getReply() {
//        var replies = replyService.getReplies();
//        return ResponseEntity.ok(replies);
//    }

    @GetMapping("/{replyId}")
    public ResponseEntity<Reply> getReplyByReplyId(@PathVariable Long replyId) {
        var reply = replyService.getReplyByReplyId(replyId);
        return ResponseEntity.ok(reply);
    }

    @GetMapping()
    public ResponseEntity<List<Reply>> getReplyByPostId(@PathVariable Long postId) {
        var replies = replyService.getReplyByPostId(postId);
        return ResponseEntity.ok(replies);
    }

//    @GetMapping("/{username}")
//    public ResponseEntity<List<Reply>> getReplyByUsername(@PathVariable String username) {
//        var replies = replyService.getReplyByUsername(username);
//        return ResponseEntity.ok(replies);
//    }



    // POST /posts
    @PostMapping("")
    public ResponseEntity<Reply> createReply(
            @PathVariable Long postId,
            @RequestBody ReplyPostRequestBody replyPostRequestBody,
            Authentication authentication) {
        var reply = replyService.createReply(postId,replyPostRequestBody,
                (UserEntity)authentication.getPrincipal());
        return ResponseEntity.ok(reply);
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<Reply> updateReply(
            @PathVariable Long postId,
            @PathVariable Long replyId,
            @RequestBody ReplyPostRequestBody replyPostRequestBody,
            Authentication authentication) {
        var reply = replyService.updateReply( postId, replyId, replyPostRequestBody,
                (UserEntity) authentication.getPrincipal());

        return ResponseEntity.ok(reply);

    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId, Authentication authentication) {
        replyService.deleteReply(replyId,(UserEntity) authentication.getPrincipal(), (PostEntity) authentication.getPrincipal());
        return ResponseEntity.noContent().build();
    }
}
