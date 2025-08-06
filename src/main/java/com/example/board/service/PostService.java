package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.entity.PostEntity;
import com.example.board.repository.PostEntityRepository;
import com.example.board.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;

    private final UserEntityRepository userEntityRepository;


    public List<Post> getPosts() {
        var postEntities = postEntityRepository.findAll();
        return postEntities.stream().map(Post::from).toList();
    }

    public Post getPostByPostId(Long postId) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        return Post.from(postEntity);

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

    public List<Post> getPostByUsername(String username) {
        UserEntity findEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return postEntityRepository.findByUser(findEntity).stream()
                .map(Post::from).toList();

    }
}
