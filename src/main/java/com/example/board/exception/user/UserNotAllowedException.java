package com.example.board.exception.user;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserNotAllowedException extends ClientErrorException {
    public UserNotAllowedException() {
        super(HttpStatus.FORBIDDEN,"User not allowed.");
    }

    public UserNotAllowedException(Long userId) {
        super(HttpStatus.FORBIDDEN,"User with userId" +userId+ "not allowed.");
    }

    public UserNotAllowedException(String userName) {
        super(HttpStatus.FORBIDDEN,"User with userName" +userName+ "not allowed.");
    }
}
