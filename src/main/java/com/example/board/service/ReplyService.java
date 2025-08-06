package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.reply.ReplyNotFoundException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.reply.Reply;
import com.example.board.model.reply.ReplyPostRequestBody;
import com.example.board.repository.PostEntityRepository;
import com.example.board.repository.ReplyEntityRepository;
import com.example.board.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final PostEntityRepository postEntityRepository;

    private final UserEntityRepository userEntityRepository;

    private final ReplyEntityRepository replyEntityRepository;


    public List<Reply> getRepliesByPostId(Long postId) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        return replyEntityRepository.findByPost(postEntity).stream()
                .map(Reply::from).toList();

    }

    public List<Reply> getRepliesByUser(String username) {
        var userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);


        var replyEntities = replyEntityRepository.findByUser(userEntity);

        return replyEntities.stream().map(Reply::from).toList();

    }

    @Transactional
    public Reply createReply(Long postId, ReplyPostRequestBody replyPostRequestBody, UserEntity currentUser) {

        var currentPost = postEntityRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        var savedReplyEntity = replyEntityRepository.save(
                ReplyEntity.of(replyPostRequestBody.body(), currentUser, currentPost)
        );
        currentPost.setRepliesCount(currentPost.getRepliesCount() + 1);

        return Reply.from(savedReplyEntity);

    }

    public Reply updateReply(Long postId, Long replyId, ReplyPostRequestBody replyPostRequestBody, UserEntity currentUser) {
        var replyEntity = replyEntityRepository.findById(replyId)
                .orElseThrow(ReplyNotFoundException::new);

        var currentPost = postEntityRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (replyEntity.getUser().equals(currentUser) && replyEntity.getPost().equals(currentPost)) {
            replyEntity.setBody(replyPostRequestBody.body());
            var updatedReplyEntity = replyEntityRepository.save(replyEntity);
            return Reply.from(updatedReplyEntity);
        }
        else{
            throw new UserNotAllowedException();
        }


    }

    public void deleteReply(Long replyId, UserEntity currentUser, PostEntity currentPost) {
        var replyEntity = replyEntityRepository.findById(replyId)
                .orElseThrow(ReplyNotFoundException::new);

        if (replyEntity.getUser().equals(currentUser) && replyEntity.getPost().equals(currentPost)) {
            replyEntityRepository.delete(replyEntity);
        }else{
            throw new UserNotAllowedException();
        }

    }

    public List<Reply> getReplyByUsername(String username) {
        UserEntity findEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return replyEntityRepository.findByUser(findEntity).stream()
                .map(Reply::from).toList();


    }


}
