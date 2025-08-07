package com.example.board.exception.follow;

import com.example.board.exception.ClientErrorException;
import com.example.board.model.entity.UserEntity;
import org.springframework.http.HttpStatus;

public class FollowNotExistException extends ClientErrorException {

    public FollowNotExistException() {
        super(HttpStatus.BAD_REQUEST,"Follow가 되어있지 않습니다..");
    }

    public FollowNotExistException(String message) {
        super(HttpStatus.NOT_FOUND,message);
    }

    public FollowNotExistException(UserEntity follower, UserEntity following) {
        super(HttpStatus.NOT_FOUND,follower.getUsername() + "와 " + following.getUsername() + " 은 follow 가 되어있지 않습니다 .");
    }
}
