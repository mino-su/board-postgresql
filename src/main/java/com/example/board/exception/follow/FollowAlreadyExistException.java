package com.example.board.exception.follow;

import com.example.board.exception.ClientErrorException;
import com.example.board.model.entity.UserEntity;
import org.springframework.http.HttpStatus;

public class FollowAlreadyExistException extends ClientErrorException {

    public FollowAlreadyExistException() {
        super(HttpStatus.BAD_REQUEST,"Follow가 이미 되어 있습니다.");
    }

    public FollowAlreadyExistException(String message) {
        super(HttpStatus.BAD_REQUEST,message);
    }

    public FollowAlreadyExistException(UserEntity follower, UserEntity following) {
        super(HttpStatus.BAD_REQUEST,follower.getUsername() + "와 " + following.getUsername() + " 은 이미 follow 되어있습니다.");
    }
}
