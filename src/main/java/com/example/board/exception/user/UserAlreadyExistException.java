package com.example.board.exception.user;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistException extends ClientErrorException {
    public UserAlreadyExistException() {
        super(HttpStatus.CONFLICT,"User already Exist.");
    }

    public UserAlreadyExistException(Long userId) {
        super(HttpStatus.NOT_FOUND,"User with userId" +userId+ "already Exist.");
    }

    public UserAlreadyExistException(String userName) {
        super(HttpStatus.NOT_FOUND,"User with userName" +userName+ "already Exist.");
    }
}
