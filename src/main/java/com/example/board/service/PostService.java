package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.model.Post;
import com.example.board.model.PostPostRequestBody;
import com.example.board.model.entity.PostEntity;
import com.example.board.repository.PostEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;


    public List<Post> getPosts() {
        var postEntities = postEntityRepository.findAll();
        return postEntities.stream().map(Post::from).toList();
    }

    public Post getPostByPostId(Long postId) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        return Post.from(postEntity);

    }

    public Post createPost(PostPostRequestBody postPostRequestBody) {

        var postEntity = new PostEntity();
        postEntity.setBody(postPostRequestBody.body());
        PostEntity save = postEntityRepository.save(postEntity);
        return Post.from(save);

    }

    public Post updatePost(Long postId, PostPostRequestBody postPostRequestBody) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));

        postEntity.setBody(postPostRequestBody.body());
        var updatedPostEntity
                = postEntityRepository.save(postEntity);
        return Post.from(updatedPostEntity);
    }

    public void deletePost(Long postId) {
        var postEntity = postEntityRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));

        postEntityRepository.delete(postEntity);
    }
}
