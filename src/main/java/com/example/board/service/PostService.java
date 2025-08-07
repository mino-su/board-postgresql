package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.LikeEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.entity.PostEntity;
import com.example.board.repository.LikeEntityRepository;
import com.example.board.repository.PostEntityRepository;
import com.example.board.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;

    private final UserEntityRepository userEntityRepository;

    private final LikeEntityRepository likeEntityRepository;


    public List<Post> getPosts(UserEntity currentUser) {
        var postEntities = postEntityRepository.findAll();
        return postEntities.stream().map(
                postEntity ->
                        Post.from(postEntity,
                                likeEntityRepository.findByUserAndPost(currentUser,postEntity).isPresent())).toList();
    }

    public Post getPostByPostId(Long postId, UserEntity currentUser) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        var isLiking = likeEntityRepository.findByUserAndPost(currentUser, postEntity).isPresent();
        return Post.from(postEntity,isLiking);

    }

    public Post createPost(PostPostRequestBody postPostRequestBody, UserEntity currentUser) {

        var savedPostEntity = postEntityRepository.save(
                PostEntity.of(postPostRequestBody.body(), currentUser)
        );

        return Post.from(savedPostEntity);

    }

    public Post updatePost(Long postId, PostPostRequestBody postPostRequestBody, UserEntity currentUser) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));

        if (postEntity.getUser().equals(currentUser)) {
            postEntity.setBody(postPostRequestBody.body());
            var updatedPostEntity
                    = postEntityRepository.save(postEntity);
            return Post.from(updatedPostEntity);
        }else{
            throw new UserNotAllowedException();
        }

    }

    public void deletePost(Long postId, UserEntity currentUser) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));

        if(postEntity.getUser().equals(currentUser)){
            postEntityRepository.delete(postEntity);

        } else {
            throw new UserNotAllowedException();
        }

    }

    public List<Post> getPostByUsername(String username, UserEntity currentUser) {
        UserEntity findEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return postEntityRepository.findByUser(findEntity).stream()
                .map(postEntity ->
                        Post.from(postEntity,likeEntityRepository.findByUserAndPost(currentUser,postEntity).isPresent())).toList();

    }

    @Transactional
    public Post toggleLike(Long postId, UserEntity currentUser) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        Optional<LikeEntity> likeEntity = likeEntityRepository.findByUserAndPost(currentUser, postEntity);

        if (likeEntity.isPresent()) {
            // 이미 존재한다면 삭제
            likeEntityRepository.delete(likeEntity.get());
            postEntity.setLikesCount(Math.max(0, postEntity.getRepliesCount() - 1));
            postEntityRepository.save(postEntity);
            return Post.from(postEntity,false);
        } else {
            // 없으면 새로 생성
            likeEntityRepository.save(LikeEntity.of(currentUser, postEntity));
            postEntity.setLikesCount(postEntity.getLikesCount() + 1);
            postEntityRepository.save(postEntity);
            return Post.from(postEntity,true);
        }

    }
}
